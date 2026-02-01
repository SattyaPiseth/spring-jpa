package co.istad.springdatajpa.controller;

import co.istad.springdatajpa.dto.request.AttributeValueRequest;
import co.istad.springdatajpa.dto.response.AttributeValueResponse;
import co.istad.springdatajpa.dto.response.ProductVariantResponse;
import co.istad.springdatajpa.config.SpringDataWebConfig;
import co.istad.springdatajpa.error.RestExceptionHandler;
import co.istad.springdatajpa.service.ProductService;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VariantController.class)
@Import({SpringDataWebConfig.class, RestExceptionHandler.class})
class VariantControllerTest {

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createVariantAttribute_success_returnsCreated() throws Exception {
        UUID variantId = UUID.randomUUID();
        UUID attributeId = UUID.randomUUID();
        AttributeValueResponse response = new AttributeValueResponse(
                attributeId,
                co.istad.springdatajpa.entity.AttributeDataType.STRING,
                co.istad.springdatajpa.entity.AttributeScope.VARIANT,
                "Red",
                null,
                null
        );
        when(productService.createVariantAttribute(eq(variantId), any(AttributeValueRequest.class))).thenReturn(response);

        mockMvc.perform(post("/variants/{id}/attributes", variantId)
                        .contentType(JSON)
                        .content("{\"attributeId\":\"" + attributeId + "\",\"valueString\":\"Red\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attributeId").value(attributeId.toString()));
    }

    @Test
    void getVariant_returnsResponse() throws Exception {
        UUID variantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ProductVariantResponse response = new ProductVariantResponse(
                variantId,
                productId,
                "SKU-3",
                new BigDecimal("29.99"),
                new BigDecimal("29.99"),
                3,
                java.time.Instant.parse("2025-01-01T00:00:00Z"),
                java.time.Instant.parse("2025-01-02T00:00:00Z")
        );
        when(productService.getVariant(variantId)).thenReturn(response);

        mockMvc.perform(get("/variants/{id}", variantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-3"));
    }

    @Test
    void listVariantAttributes_returnsArray() throws Exception {
        UUID variantId = UUID.randomUUID();
        UUID attributeId = UUID.randomUUID();
        AttributeValueResponse response = new AttributeValueResponse(
                attributeId,
                co.istad.springdatajpa.entity.AttributeDataType.STRING,
                co.istad.springdatajpa.entity.AttributeScope.VARIANT,
                "Blue",
                null,
                null
        );
        when(productService.listVariantAttributes(variantId)).thenReturn(List.of(response));

        mockMvc.perform(get("/variants/{id}/attributes", variantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attributeId").value(attributeId.toString()));
    }
}
