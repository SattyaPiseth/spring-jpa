package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.ProductVariant;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Page<ProductVariant> findAllByProductId(UUID productId, Pageable pageable);

    @Query("""
            select v
            from ProductVariant v
            where v.product.id = :productId
            order by v.createdAt desc, v.id desc
            """)
    List<ProductVariant> findKeysetFirstPage(@Param("productId") UUID productId, Pageable pageable);

    @Query("""
            select v
            from ProductVariant v
            where v.product.id = :productId
              and (v.createdAt < :createdAt or (v.createdAt = :createdAt and v.id < :id))
            order by v.createdAt desc, v.id desc
            """)
    List<ProductVariant> findKeysetNextPage(@Param("productId") UUID productId,
                                            @Param("createdAt") Instant createdAt,
                                            @Param("id") UUID id,
                                            Pageable pageable);
}
