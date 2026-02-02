package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.Product;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
            select distinct p
            from Product p
            join p.categories c
            where c.id = :categoryId
            """)
    Page<Product> findAllByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
            select p
            from Product p
            order by p.createdAt desc, p.id desc
            """)
    List<Product> findKeysetFirstPageNoCategory(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
            select p
            from Product p
            where (p.createdAt < :createdAt or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<Product> findKeysetNextPageNoCategory(@Param("createdAt") Instant createdAt,
                                               @Param("id") UUID id,
                                               Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
            select distinct p
            from Product p
            join p.categories c
            where c.id = :categoryId
            order by p.createdAt desc, p.id desc
            """)
    List<Product> findKeysetFirstPageByCategory(@Param("categoryId") UUID categoryId, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
            select distinct p
            from Product p
            join p.categories c
            where c.id = :categoryId
              and (p.createdAt < :createdAt or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<Product> findKeysetNextPageByCategory(@Param("categoryId") UUID categoryId,
                                               @Param("createdAt") Instant createdAt,
                                               @Param("id") UUID id,
                                               Pageable pageable);
}

