package co.istad.springdatajpa.controller;

import co.istad.springdatajpa.dto.request.AttributeValueRequest;
import co.istad.springdatajpa.dto.response.AttributeValueResponse;
import co.istad.springdatajpa.dto.response.ProductVariantResponse;
import co.istad.springdatajpa.service.ProductService;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/variants")
public class VariantController {

    private final ProductService productService;

    public VariantController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{id}/attributes")
    public ResponseEntity<AttributeValueResponse> createVariantAttribute(
            @PathVariable UUID id,
            @Valid @RequestBody AttributeValueRequest request
    ) {
        AttributeValueResponse response = productService.createVariantAttribute(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantResponse> getVariant(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getVariant(id));
    }

    @GetMapping("/{id}/attributes")
    public List<AttributeValueResponse> listVariantAttributes(@PathVariable UUID id) {
        return productService.listVariantAttributes(id);
    }

    @PutMapping("/{id}/attributes/{attributeId}")
    public ResponseEntity<AttributeValueResponse> updateVariantAttribute(
            @PathVariable UUID id,
            @PathVariable UUID attributeId,
            @Valid @RequestBody AttributeValueRequest request
    ) {
        AttributeValueResponse response = productService.updateVariantAttribute(id, attributeId, request);
        return ResponseEntity.ok(response);
    }
}
