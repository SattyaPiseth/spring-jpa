package co.istad.springdatajpa.controller;

import co.istad.springdatajpa.dto.request.CategoryCreateRequest;
import co.istad.springdatajpa.dto.request.CategoryPatchRequest;
import co.istad.springdatajpa.dto.response.CategoryResponse;
import co.istad.springdatajpa.dto.request.CategoryUpdateRequest;
import co.istad.springdatajpa.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private static final int MIN_PAGE = 0;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "updatedAt",
            "name"
    );

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Page<CategoryResponse> listCategories(
            @RequestParam(defaultValue = "0") @Min(MIN_PAGE) int page,
            @RequestParam(defaultValue = "20") @Min(MIN_SIZE) @Max(MAX_SIZE) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        PageRequest pageable = ControllerUtils.pageRequest(page, size, sort, ALLOWED_SORT_FIELDS, defaultSort());
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> patchCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryPatchRequest request
    ) {
        CategoryResponse response = categoryService.patch(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort defaultSort() {
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}

