param(
    [int]$TimeoutSeconds = 40,
    [switch]$SkipBootRuns,
    [switch]$SkipTests,
    [switch]$SkipIntegrationTests
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$reportsDir = Join-Path $repoRoot "reports"
if (-not (Test-Path $reportsDir)) {
    New-Item -ItemType Directory -Path $reportsDir | Out-Null
}

$gradleCmd = Join-Path $repoRoot "gradlew.bat"

$gradleUserHome = Join-Path $repoRoot ".gradle-user-home"
if (-not (Test-Path $gradleUserHome)) {
    New-Item -ItemType Directory -Path $gradleUserHome | Out-Null
}
$env:GRADLE_USER_HOME = $gradleUserHome

function Load-EnvFile() {
    $envFile = Join-Path $repoRoot ".env.file"
    if (-not (Test-Path $envFile)) {
        return
    }
    $lines = Get-Content $envFile
    foreach ($line in $lines) {
        if ($line -match "^\s*#") { continue }
        $parts = $line -split "=", 2
        if ($parts.Length -ne 2) { continue }
        $key = $parts[0].Trim()
        $value = $parts[1].Trim()
        if (-not [string]::IsNullOrWhiteSpace($key)) {
            Set-Item -Path "Env:$key" -Value $value
        }
    }
}

Load-EnvFile

$script:summary = @()
function Add-Summary([string]$line) {
    $script:summary += $line
}
function Add-Section([string]$title) {
    $script:summary += ""
    $script:summary += "## $title"
}
function Format-Duration([datetime]$start, [datetime]$end) {
    $span = $end - $start
    return ("{0:00}:{1:00}:{2:00}" -f $span.Hours, $span.Minutes, $span.Seconds)
}

function Write-Status([string]$message) {
    Write-Output $message
}

function Test-DockerAvailable() {
    $candidates = @()
    $cmd = Get-Command docker -ErrorAction SilentlyContinue
    if ($cmd -and $cmd.Source) {
        $candidates += $cmd.Source
    }
    $candidates += @(
        "$env:ProgramFiles\Docker\Docker\resources\bin\docker.exe",
        "$env:ProgramFiles\Docker\Docker\resources\bin\docker.exe"
    )

    foreach ($path in $candidates) {
        if (-not $path) {
            continue
        }
        if (Test-Path $path) {
            $prev = $ErrorActionPreference
            $ErrorActionPreference = "SilentlyContinue"
            $null = & $path info 2>$null
            $ErrorActionPreference = $prev
            if ($LASTEXITCODE -eq 0) {
                return $true
            }
        }
    }

    return $false
}

function Test-PostgresEnv() {
    $required = @("POSTGRES_HOST", "POSTGRES_PORT", "POSTGRES_DB", "POSTGRES_USER", "POSTGRES_PASSWORD")
    $envFile = Join-Path $repoRoot ".env.file"
    $envMap = @{}
    if (Test-Path $envFile) {
        $lines = Get-Content $envFile
        foreach ($line in $lines) {
            if ($line -match "^\s*#") { continue }
            $parts = $line -split "=", 2
            if ($parts.Length -ne 2) { continue }
            $key = $parts[0].Trim()
            $value = $parts[1].Trim()
            if (-not [string]::IsNullOrWhiteSpace($key)) {
                $envMap[$key] = $value
            }
        }
    }
    foreach ($name in $required) {
        $current = ${env:$name}
        if ([string]::IsNullOrWhiteSpace($current) -and $envMap.ContainsKey($name)) {
            $current = $envMap[$name]
        }
        if ([string]::IsNullOrWhiteSpace($current)) {
            return $false
        }
    }
    return $true
}

function Invoke-BootCheck([string]$profile, [int]$timeoutSeconds) {
    $logPath = Join-Path $reportsDir "boot-$profile.log"
    $errPath = Join-Path $reportsDir "boot-$profile.err.log"
    if (Test-Path $logPath) {
        try {
            Remove-Item $logPath -Force
        } catch {
            $logPath = Join-Path $reportsDir ("boot-$profile-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".log")
        }
    }
    if (Test-Path $errPath) {
        try {
            Remove-Item $errPath -Force
        } catch {
            $errPath = Join-Path $reportsDir ("boot-$profile-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".err.log")
        }
    }

    $args = @(
        "bootRun",
        "-Dspring-boot.run.profiles=$profile"
    )

    $process = Start-Process -FilePath $gradleCmd `
        -ArgumentList $args `
        -RedirectStandardOutput $logPath `
        -RedirectStandardError $errPath `
        -WorkingDirectory $repoRoot `
        -PassThru

    $started = $false
    $failed = $false
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)

    while ((Get-Date) -lt $deadline -and -not $process.HasExited) {
        Start-Sleep -Milliseconds 500
        if (Test-Path $logPath) {
            $content = Get-Content $logPath -Raw
            if ($content -match "Started .* in" -or $content -match "Started .*\(JVM running") {
                $started = $true
                break
            }
            if ($content -match "APPLICATION FAILED" -or $content -match "FAILED" -or $content -match "Exception") {
                $failed = $true
                break
            }
        }
    }

    if (-not $process.HasExited) {
        Stop-Process -Id $process.Id -Force
    }

    $result = "timeout"
    if ($failed) {
        $result = "failed"
    } elseif ($started) {
        $result = "started"
    } elseif ($process.ExitCode -eq 0) {
        $result = "exited"
    }

    return [pscustomobject]@{
        Result = $result
        Log    = $logPath
        Err    = $errPath
    }
}

if (-not $SkipTests) {
    Add-Section "Tests"
    Write-Status "Running fast tests (H2): ./gradlew clean test"
    $start = Get-Date
    & $gradleCmd clean test
    $end = Get-Date
    if ($LASTEXITCODE -ne 0) {
        Add-Summary "- Fast tests: FAIL (duration $(Format-Duration $start $end))"
        throw "Fast tests failed. See build/reports/tests/test/index.html"
    }
    Add-Summary "- Fast tests: PASS (duration $(Format-Duration $start $end))"

    if (-not $SkipIntegrationTests) {
        if (-not (Test-DockerAvailable)) {
            Write-Status "Skipping integration tests (Docker not available)."
            Add-Summary "- Integration tests: SKIPPED (Docker not available)"
        } else {
            Write-Status "Running integration tests (Testcontainers): ./gradlew -Dit.tc=true clean integrationTest"
            $start = Get-Date
            & $gradleCmd --% -Dit.tc=true clean integrationTest
            $end = Get-Date
            if ($LASTEXITCODE -ne 0) {
                Add-Summary "- Integration tests: FAIL (duration $(Format-Duration $start $end))"
                throw "Integration tests failed. See build/reports/tests/integrationTest/index.html"
            }
            Add-Summary "- Integration tests: PASS (duration $(Format-Duration $start $end))"
        }
    }
}

if (-not $SkipBootRuns) {
    Add-Section "Boot Checks"
    $profiles = @("default", "dev", "local", "staging", "prod")
    $needsPostgres = @("default", "staging", "prod")
    $pgOk = Test-PostgresEnv

    foreach ($profile in $profiles) {
        if ($needsPostgres -contains $profile -and -not $pgOk) {
            Write-Status "Skipping $profile (POSTGRES_* env vars are not set)"
            Add-Summary "- Boot check ($profile): SKIPPED (missing POSTGRES_* env vars)"
            continue
        }

        Write-Status "Boot check: $profile"
        $start = Get-Date
        $boot = Invoke-BootCheck -profile $profile -timeoutSeconds $TimeoutSeconds
        $end = Get-Date
        Write-Status "Result: $profile -> $($boot.Result) (log: $($boot.Log))"
        Add-Summary "- Boot check ($profile): $($boot.Result) (duration $(Format-Duration $start $end))"
        Add-Summary "  - log: $($boot.Log)"
        Add-Summary "  - err: $($boot.Err)"

        if ($boot.Result -eq "failed") {
            throw "Boot check failed for profile $profile. See $($boot.Log)"
        }
    }
}

if ($script:summary.Count -gt 0) {
    $summaryPath = Join-Path $reportsDir "run-all-profiles-summary.md"
    $content = @(
        "# Run Summary"
        ""
        ("Date: " + (Get-Date -Format "yyyy-MM-dd HH:mm:ss"))
        ""
    ) + $script:summary
    $content | Set-Content -Path $summaryPath -Encoding ASCII
}

Write-Status "Done."
