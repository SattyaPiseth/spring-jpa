package co.istad.springdatajpa.service.impl;

import co.istad.springdatajpa.dto.request.AttributeValueRequest;
import co.istad.springdatajpa.dto.request.ProductCreateRequest;
import co.istad.springdatajpa.dto.request.ProductPatchRequest;
import co.istad.springdatajpa.dto.request.ProductUpdateRequest;
import co.istad.springdatajpa.dto.request.ProductVariantCreateRequest;
import co.istad.springdatajpa.dto.request.ProductVariantUpdateRequest;
import co.istad.springdatajpa.dto.response.AttributeValueResponse;
import co.istad.springdatajpa.dto.response.KeysetResponse;
import co.istad.springdatajpa.dto.response.ProductResponse;
import co.istad.springdatajpa.dto.response.ProductVariantResponse;
import co.istad.springdatajpa.entity.AttributeDefinition;
import co.istad.springdatajpa.entity.AttributeDataType;
import co.istad.springdatajpa.entity.AttributeScope;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.entity.ProductAttributeValue;
import co.istad.springdatajpa.entity.ProductAttributeValueId;
import co.istad.springdatajpa.entity.ProductVariant;
import co.istad.springdatajpa.entity.VariantAttributeValue;
import co.istad.springdatajpa.entity.VariantAttributeValueId;
import co.istad.springdatajpa.exception.BadRequestException;
import co.istad.springdatajpa.exception.ResourceNotFoundException;
import co.istad.springdatajpa.mapper.ProductMapper;
import co.istad.springdatajpa.repository.AttributeDefinitionRepository;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductAttributeValueRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import co.istad.springdatajpa.repository.ProductVariantRepository;
import co.istad.springdatajpa.repository.VariantAttributeValueRepository;
import co.istad.springdatajpa.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import co.istad.springdatajpa.util.KeysetCursor;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              ProductVariantRepository productVariantRepository,
                              AttributeDefinitionRepository attributeDefinitionRepository,
                              ProductAttributeValueRepository productAttributeValueRepository,
                              VariantAttributeValueRepository variantAttributeValueRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.productVariantRepository = productVariantRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.variantAttributeValueRepository = variantAttributeValueRepository;
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
    public KeysetResponse<ProductResponse> listProductsKeyset(UUID categoryId, String cursor, int size) {
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        List<Product> items = fetchProductsKeyset(categoryId, cursor, size + 1);
        boolean hasNext = items.size() > size;
        if (hasNext) {
            items = items.subList(0, size);
        }
        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            Product last = items.get(items.size() - 1);
            nextCursor = KeysetCursor.encode(last.getCreatedAt(), last.getId());
        }
        List<ProductResponse> mapped = items.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        return new KeysetResponse<>(mapped, nextCursor, hasNext);
    }

    @Override
    public ProductResponse findById(UUID id) {
        return productMapper.toResponse(getProductOrThrow(id));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        applyCategories(product, request.categoryId(), request.categoryIds());
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        Product product = getProductOrThrow(id);
        productMapper.updateEntity(request, product);
        applyCategories(product, request.categoryId(), request.categoryIds());
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

    @Override
    @Transactional
    public ProductVariantResponse createVariant(UUID productId, ProductVariantCreateRequest request) {
        Product product = getProductOrThrow(productId);
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.sku());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        ProductVariant saved = productVariantRepository.save(variant);
        return toVariantResponse(saved);
    }

    @Override
    @Transactional
    public ProductVariantResponse updateVariant(UUID productId, UUID variantId, ProductVariantUpdateRequest request) {
        ProductVariant variant = getVariantOrThrow(variantId);
        if (!variant.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Variant not found for product: " + productId);
        }
        variant.setSku(request.sku());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        return toVariantResponse(variant);
    }

    @Override
    @Transactional
    public AttributeValueResponse createProductAttribute(UUID productId, AttributeValueRequest request) {
        UUID attributeId = requireAttributeId(request);
        Product product = getProductOrThrow(productId);
        AttributeDefinition definition = getAttributeOrThrow(attributeId);
        validateAttribute(definition, request, AttributeScope.PRODUCT);
        ProductAttributeValueId id = new ProductAttributeValueId(productId, attributeId);
        if (productAttributeValueRepository.existsById(id)) {
            throw new BadRequestException("Product attribute already exists: " + attributeId);
        }
        ProductAttributeValue value = new ProductAttributeValue();
        value.setId(id);
        value.setProduct(product);
        value.setAttribute(definition);
        applyTypedValue(definition, request, value);
        ProductAttributeValue saved = productAttributeValueRepository.save(value);
        return toAttributeResponse(saved.getAttribute(), saved.getValueString(), saved.getValueNumber(), saved.getValueBoolean());
    }

    @Override
    @Transactional
    public AttributeValueResponse updateProductAttribute(UUID productId, UUID attributeId, AttributeValueRequest request) {
        if (request.attributeId() != null && !request.attributeId().equals(attributeId)) {
            throw new BadRequestException("attributeId in path and body must match");
        }
        ProductAttributeValueId id = new ProductAttributeValueId(productId, attributeId);
        ProductAttributeValue value = productAttributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product attribute not found: " + attributeId));
        AttributeDefinition definition = value.getAttribute();
        validateAttribute(definition, request, AttributeScope.PRODUCT);
        applyTypedValue(definition, request, value);
        return toAttributeResponse(definition, value.getValueString(), value.getValueNumber(), value.getValueBoolean());
    }

    @Override
    @Transactional
    public AttributeValueResponse createVariantAttribute(UUID variantId, AttributeValueRequest request) {
        UUID attributeId = requireAttributeId(request);
        ProductVariant variant = getVariantOrThrow(variantId);
        AttributeDefinition definition = getAttributeOrThrow(attributeId);
        validateAttribute(definition, request, AttributeScope.VARIANT);
        VariantAttributeValueId id = new VariantAttributeValueId(variantId, attributeId);
        if (variantAttributeValueRepository.existsById(id)) {
            throw new BadRequestException("Variant attribute already exists: " + attributeId);
        }
        VariantAttributeValue value = new VariantAttributeValue();
        value.setId(id);
        value.setVariant(variant);
        value.setAttribute(definition);
        applyTypedValue(definition, request, value);
        VariantAttributeValue saved = variantAttributeValueRepository.save(value);
        return toAttributeResponse(saved.getAttribute(), saved.getValueString(), saved.getValueNumber(), saved.getValueBoolean());
    }

    @Override
    @Transactional
    public AttributeValueResponse updateVariantAttribute(UUID variantId, UUID attributeId, AttributeValueRequest request) {
        if (request.attributeId() != null && !request.attributeId().equals(attributeId)) {
            throw new BadRequestException("attributeId in path and body must match");
        }
        VariantAttributeValueId id = new VariantAttributeValueId(variantId, attributeId);
        VariantAttributeValue value = variantAttributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant attribute not found: " + attributeId));
        AttributeDefinition definition = value.getAttribute();
        validateAttribute(definition, request, AttributeScope.VARIANT);
        applyTypedValue(definition, request, value);
        return toAttributeResponse(definition, value.getValueString(), value.getValueNumber(), value.getValueBoolean());
    }

    @Override
    public Page<ProductVariantResponse> listVariants(UUID productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
        return productVariantRepository.findAllByProductId(productId, pageable)
                .map(this::toVariantResponse);
    }

    @Override
    public KeysetResponse<ProductVariantResponse> listVariantsKeyset(UUID productId, String cursor, int size) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
        List<ProductVariant> items = fetchVariantsKeyset(productId, cursor, size + 1);
        boolean hasNext = items.size() > size;
        if (hasNext) {
            items = items.subList(0, size);
        }
        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            ProductVariant last = items.get(items.size() - 1);
            nextCursor = KeysetCursor.encode(last.getCreatedAt(), last.getId());
        }
        List<ProductVariantResponse> mapped = items.stream()
                .map(this::toVariantResponse)
                .collect(Collectors.toList());
        return new KeysetResponse<>(mapped, nextCursor, hasNext);
    }

    @Override
    public ProductVariantResponse getVariant(UUID variantId) {
        return toVariantResponse(getVariantOrThrow(variantId));
    }

    @Override
    public List<AttributeValueResponse> listProductAttributes(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
        return productAttributeValueRepository.findAllByProductId(productId).stream()
                .map(value -> toAttributeResponse(
                        value.getAttribute(),
                        value.getValueString(),
                        value.getValueNumber(),
                        value.getValueBoolean()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttributeValueResponse> listVariantAttributes(UUID variantId) {
        if (!productVariantRepository.existsById(variantId)) {
            throw new ResourceNotFoundException("Variant not found: " + variantId);
        }
        return variantAttributeValueRepository.findAllByVariantId(variantId).stream()
                .map(value -> toAttributeResponse(
                        value.getAttribute(),
                        value.getValueString(),
                        value.getValueNumber(),
                        value.getValueBoolean()
                ))
                .collect(Collectors.toList());
    }

    private void applyCategories(Product product, UUID primaryCategoryId, Set<UUID> categoryIds) {
        if (primaryCategoryId != null) {
            Category primary = categoryRepository.findById(primaryCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + primaryCategoryId));
            product.setCategory(primary);
        }

        if (categoryIds != null) {
            Set<Category> resolved = new HashSet<>();
            for (UUID categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
                resolved.add(category);
            }
            product.clearCategories();
            for (Category category : resolved) {
                product.addCategory(category);
            }
            if (product.getCategory() != null && !product.getCategories().contains(product.getCategory())) {
                product.addCategory(product.getCategory());
            }
        }
    }

    private Product getProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private ProductVariant getVariantOrThrow(UUID id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + id));
    }

    private List<Product> fetchProductsKeyset(UUID categoryId, String cursor, int size) {
        if (cursor == null || cursor.isBlank()) {
            return productRepository.findKeysetFirstPage(categoryId, PageRequest.of(0, size));
        }
        KeysetCursor.Decoded decoded = KeysetCursor.decode(cursor);
        return productRepository.findKeysetNextPage(
                categoryId,
                decoded.createdAt(),
                decoded.id(),
                PageRequest.of(0, size)
        );
    }

    private List<ProductVariant> fetchVariantsKeyset(UUID productId, String cursor, int size) {
        if (cursor == null || cursor.isBlank()) {
            return productVariantRepository.findKeysetFirstPage(productId, PageRequest.of(0, size));
        }
        KeysetCursor.Decoded decoded = KeysetCursor.decode(cursor);
        return productVariantRepository.findKeysetNextPage(
                productId,
                decoded.createdAt(),
                decoded.id(),
                PageRequest.of(0, size)
        );
    }

    private AttributeDefinition getAttributeOrThrow(UUID id) {
        return attributeDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found: " + id));
    }

    private UUID requireAttributeId(AttributeValueRequest request) {
        if (request.attributeId() == null) {
            throw new BadRequestException("attributeId is required");
        }
        return request.attributeId();
    }

    private void validateAttribute(AttributeDefinition definition, AttributeValueRequest request, AttributeScope targetScope) {
        if (targetScope == AttributeScope.PRODUCT && definition.getScope() == AttributeScope.VARIANT) {
            throw new BadRequestException("Attribute scope does not allow product assignment");
        }
        if (targetScope == AttributeScope.VARIANT && definition.getScope() == AttributeScope.PRODUCT) {
            throw new BadRequestException("Attribute scope does not allow variant assignment");
        }
        AttributeDataType dataType = definition.getDataType();
        switch (dataType) {
            case STRING -> {
                if (request.valueString() == null) {
                    throw new BadRequestException("valueString is required for STRING attributes");
                }
            }
            case NUMBER -> {
                if (request.valueNumber() == null) {
                    throw new BadRequestException("valueNumber is required for NUMBER attributes");
                }
            }
            case BOOLEAN -> {
                if (request.valueBoolean() == null) {
                    throw new BadRequestException("valueBoolean is required for BOOLEAN attributes");
                }
            }
            default -> throw new BadRequestException("Unsupported attribute data type");
        }
    }

    private void applyTypedValue(AttributeDefinition definition, AttributeValueRequest request, ProductAttributeValue value) {
        value.setValueString(null);
        value.setValueNumber(null);
        value.setValueBoolean(null);
        switch (definition.getDataType()) {
            case STRING -> value.setValueString(request.valueString());
            case NUMBER -> value.setValueNumber(request.valueNumber());
            case BOOLEAN -> value.setValueBoolean(request.valueBoolean());
            default -> throw new BadRequestException("Unsupported attribute data type");
        }
    }

    private void applyTypedValue(AttributeDefinition definition, AttributeValueRequest request, VariantAttributeValue value) {
        value.setValueString(null);
        value.setValueNumber(null);
        value.setValueBoolean(null);
        switch (definition.getDataType()) {
            case STRING -> value.setValueString(request.valueString());
            case NUMBER -> value.setValueNumber(request.valueNumber());
            case BOOLEAN -> value.setValueBoolean(request.valueBoolean());
            default -> throw new BadRequestException("Unsupported attribute data type");
        }
    }

    private ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getPrice(),
                variant.getStock(),
                variant.getCreatedAt(),
                variant.getUpdatedAt()
        );
    }

    private AttributeValueResponse toAttributeResponse(AttributeDefinition definition,
                                                       String valueString,
                                                       java.math.BigDecimal valueNumber,
                                                       Boolean valueBoolean) {
        return new AttributeValueResponse(
                definition.getId(),
                definition.getDataType(),
                definition.getScope(),
                valueString,
                valueNumber,
                valueBoolean
        );
    }
}

