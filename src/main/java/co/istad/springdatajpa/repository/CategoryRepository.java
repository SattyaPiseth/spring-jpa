package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}

