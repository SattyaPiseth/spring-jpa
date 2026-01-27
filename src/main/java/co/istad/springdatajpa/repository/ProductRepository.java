package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByCategoryId(UUID categoryId, Pageable pageable);
}

