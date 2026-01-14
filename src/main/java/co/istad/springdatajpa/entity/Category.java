package co.istad.springdatajpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends AuditedBaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;
}
