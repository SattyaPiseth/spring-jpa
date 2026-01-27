package co.istad.springdatajpa.service;

import co.istad.springdatajpa.dto.request.CategoryCreateRequest;
import co.istad.springdatajpa.dto.request.CategoryPatchRequest;
import co.istad.springdatajpa.dto.response.CategoryResponse;
import co.istad.springdatajpa.dto.request.CategoryUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    Page<CategoryResponse> findAll(Pageable pageable);
    CategoryResponse findById(UUID id);
    CategoryResponse create(CategoryCreateRequest request);
    CategoryResponse update(UUID id, CategoryUpdateRequest request);
    CategoryResponse patch(UUID id, CategoryPatchRequest request);
    void delete(UUID id);
}

