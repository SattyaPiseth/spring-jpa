package co.istad.springdatajpa.service;

import co.istad.springdatajpa.dto.request.ProductCreateRequest;
import co.istad.springdatajpa.dto.request.ProductPatchRequest;
import co.istad.springdatajpa.dto.request.ProductVariantCreateRequest;
import co.istad.springdatajpa.dto.request.ProductVariantUpdateRequest;
import co.istad.springdatajpa.dto.request.AttributeValueRequest;
import co.istad.springdatajpa.dto.response.ProductResponse;
import co.istad.springdatajpa.dto.response.ProductVariantResponse;
import co.istad.springdatajpa.dto.response.AttributeValueResponse;
import co.istad.springdatajpa.dto.response.KeysetResponse;
import co.istad.springdatajpa.dto.request.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    Page<ProductResponse> findAll(Pageable pageable, UUID categoryId);
    ProductResponse findById(UUID id);
    ProductResponse create(ProductCreateRequest request);
    ProductResponse update(UUID id, ProductUpdateRequest request);
    ProductResponse patch(UUID id, ProductPatchRequest request);
    void delete(UUID id);

    ProductVariantResponse createVariant(UUID productId, ProductVariantCreateRequest request);
    ProductVariantResponse updateVariant(UUID productId, UUID variantId, ProductVariantUpdateRequest request);

    AttributeValueResponse createProductAttribute(UUID productId, AttributeValueRequest request);
    AttributeValueResponse updateProductAttribute(UUID productId, UUID attributeId, AttributeValueRequest request);

    AttributeValueResponse createVariantAttribute(UUID variantId, AttributeValueRequest request);
    AttributeValueResponse updateVariantAttribute(UUID variantId, UUID attributeId, AttributeValueRequest request);

    Page<ProductVariantResponse> listVariants(UUID productId, Pageable pageable);
    KeysetResponse<ProductVariantResponse> listVariantsKeyset(UUID productId, String cursor, int size);
    ProductVariantResponse getVariant(UUID variantId);

    List<AttributeValueResponse> listProductAttributes(UUID productId);
    List<AttributeValueResponse> listVariantAttributes(UUID variantId);

    KeysetResponse<ProductResponse> listProductsKeyset(UUID categoryId, String cursor, int size);
}

