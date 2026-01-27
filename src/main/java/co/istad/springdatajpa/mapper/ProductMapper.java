package co.istad.springdatajpa.mapper;

import co.istad.springdatajpa.dto.response.CategorySummary;
import co.istad.springdatajpa.dto.request.ProductCreateRequest;
import co.istad.springdatajpa.dto.request.ProductPatchRequest;
import co.istad.springdatajpa.dto.response.ProductResponse;
import co.istad.springdatajpa.dto.response.ProductSummary;
import co.istad.springdatajpa.dto.request.ProductUpdateRequest;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "category", source = "category")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntity(ProductUpdateRequest request, @MappingTarget Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ProductPatchRequest request, @MappingTarget Product product);

    CategorySummary toSummary(Category category);

    ProductSummary toSummary(Product product);
}

