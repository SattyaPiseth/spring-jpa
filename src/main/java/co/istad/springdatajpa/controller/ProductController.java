package co.istad.springdatajpa.controller;

import co.istad.springdatajpa.dto.ProductCreateRequest;
import co.istad.springdatajpa.dto.ProductResponse;
import co.istad.springdatajpa.dto.ProductUpdateRequest;
import co.istad.springdatajpa.dto.ProductPatchRequest;
import co.istad.springdatajpa.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/products")
public class ProductController {

    private static final int MIN_PAGE = 0;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "updatedAt",
            "name",
            "price"
    );

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductResponse> listProducts(
            @RequestParam(defaultValue = "0") @Min(MIN_PAGE) int page,
            @RequestParam(defaultValue = "20") @Min(MIN_SIZE) @Max(MAX_SIZE) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) UUID categoryId
    ) {
        PageRequest pageable = ControllerUtils.pageRequest(page, size, sort, ALLOWED_SORT_FIELDS, defaultSort());
        return productService.findAll(pageable, categoryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patchProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductPatchRequest request
    ) {
        ProductResponse response = productService.patch(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort defaultSort() {
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
