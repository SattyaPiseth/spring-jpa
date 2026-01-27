package co.istad.springdatajpa.service.impl;

import co.istad.springdatajpa.dto.ProductCreateRequest;
import co.istad.springdatajpa.dto.ProductPatchRequest;
import co.istad.springdatajpa.dto.ProductResponse;
import co.istad.springdatajpa.dto.ProductUpdateRequest;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.exception.ResourceNotFoundException;
import co.istad.springdatajpa.mapper.ProductMapper;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import co.istad.springdatajpa.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable, UUID categoryId) {
        if (categoryId == null) {
            return productRepository.findAll(pageable).map(productMapper::toResponse);
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        return productRepository.findAllByCategoryId(categoryId, pageable).map(productMapper::toResponse);
    }

    @Override
    public ProductResponse findById(UUID id) {
        return productMapper.toResponse(getProductOrThrow(id));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        applyCategoryIfPresent(product, request.categoryId());
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        Product product = getProductOrThrow(id);
        productMapper.updateEntity(request, product);
        applyCategoryIfPresent(product, request.categoryId());
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse patch(UUID id, ProductPatchRequest request) {
        Product product = getProductOrThrow(id);
        productMapper.patchEntity(request, product);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = getProductOrThrow(id);
        productRepository.delete(product);
    }

    private void applyCategoryIfPresent(Product product, UUID categoryId) {
        if (categoryId == null) {
            return;
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        product.setCategory(category);
    }

    private Product getProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
}
