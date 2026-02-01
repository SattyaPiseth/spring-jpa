package co.istad.springdatajpa.service.impl;

import co.istad.springdatajpa.dto.request.CategoryCreateRequest;
import co.istad.springdatajpa.dto.request.CategoryPatchRequest;
import co.istad.springdatajpa.dto.response.CategoryResponse;
import co.istad.springdatajpa.dto.request.CategoryUpdateRequest;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.exception.BadRequestException;
import co.istad.springdatajpa.exception.ResourceNotFoundException;
import co.istad.springdatajpa.mapper.CategoryMapper;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponseWithoutProducts);
    }

    @Override
    public CategoryResponse findById(UUID id) {
        return categoryMapper.toResponse(getCategoryOrThrow(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        Category category = categoryMapper.toEntity(request);
        applyHierarchy(category, request.parentId(), request.sortOrder());
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryUpdateRequest request) {
        Category category = getCategoryOrThrow(id);
        categoryMapper.updateEntity(request, category);
        applyHierarchy(category, request.parentId(), request.sortOrder());
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse patch(UUID id, CategoryPatchRequest request) {
        Category category = getCategoryOrThrow(id);
        categoryMapper.patchEntity(request, category);
        applyHierarchy(category, request.parentId(), request.sortOrder());
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Category category = getCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category getCategoryOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private void applyHierarchy(Category category, UUID parentId, Integer sortOrder) {
        if (parentId != null) {
            if (category.getId() != null && category.getId().equals(parentId)) {
                throw new BadRequestException("parentId must not reference the category itself");
            }
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new BadRequestException("Parent category not found: " + parentId));
            category.setParent(parent);
        }
        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }
    }
}

