package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
