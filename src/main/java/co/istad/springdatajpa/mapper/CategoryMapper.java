package co.istad.springdatajpa.mapper;

import co.istad.springdatajpa.dto.request.CategoryCreateRequest;
import co.istad.springdatajpa.dto.request.CategoryPatchRequest;
import co.istad.springdatajpa.dto.response.CategoryResponse;
import co.istad.springdatajpa.dto.request.CategoryUpdateRequest;
import co.istad.springdatajpa.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ProductMapper.class)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    @Mapping(target = "products", expression = "java(java.util.List.of())")
    CategoryResponse toResponseWithoutProducts(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryCreateRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CategoryUpdateRequest request, @MappingTarget Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(CategoryPatchRequest request, @MappingTarget Category category);
}

