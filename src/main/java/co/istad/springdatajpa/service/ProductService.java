package co.istad.springdatajpa.service;

import co.istad.springdatajpa.dto.ProductCreateRequest;
import co.istad.springdatajpa.dto.ProductResponse;
import co.istad.springdatajpa.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ProductService {
    Page<ProductResponse> findAll(Pageable pageable);
    ProductResponse create(ProductCreateRequest request);
    ProductResponse update(UUID id, ProductUpdateRequest request);
}
