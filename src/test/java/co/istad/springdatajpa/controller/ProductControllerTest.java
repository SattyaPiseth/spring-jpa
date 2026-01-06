package co.istad.springdatajpa.controller;

import co.istad.springdatajpa.dto.ProductResponse;
import co.istad.springdatajpa.dto.ProductCreateRequest;
import co.istad.springdatajpa.dto.ProductUpdateRequest;
import co.istad.springdatajpa.exception.ResourceNotFoundException;
import co.istad.springdatajpa.config.SpringDataWebConfig;
import co.istad.springdatajpa.error.RestExceptionHandler;
import co.istad.springdatajpa.service.ProductService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SpringDataWebConfig.class, RestExceptionHandler.class})
class ProductControllerTest {

    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_validationFailure_returnsErrors() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(hasItem(containsString("name"))));
    }

    @Test
    void createProduct_success_returnsCreated() throws Exception {
        ProductResponse response = newResponse("Mouse", "Wireless", "49.99");
        when(productService.create(any(ProductCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(JSON)
                        .content(requestJson("Mouse", "Wireless", "49.99")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mouse"));
    }

    @Test
    void listProducts_returnsPage() throws Exception {
        ProductResponse response = newResponse("Keyboard", "Mechanical", "99.99");
        Page<ProductResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(productService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    @Test
    void listProducts_invalidPaging_returns400() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "-1")
                        .param("size", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void listProducts_unexpectedError_returns500() throws Exception {
        when(productService.findAll(any(Pageable.class))).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.path").value("/products"));
    }

    @Test
    void updateProduct_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.update(eq(id), any(ProductUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found: " + id));

        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Monitor\",\"description\":\"4K\",\"price\":299.99}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateProduct_success_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                id,
                "Monitor",
                "4K",
                new BigDecimal("299.99"),
                CREATED_AT,
                UPDATED_AT
        );
        when(productService.update(eq(id), any(ProductUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/products/{id}", id)
                        .contentType(JSON)
                        .content(requestJson("Monitor", "4K", "299.99")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monitor"));
    }

    @Test
    void updateProduct_validationFailure_returnsErrors() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/products/{id}", id)
                        .contentType(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(hasItem(containsString("name"))));
    }

    private static ProductResponse newResponse(String name, String description, String price) {
        return new ProductResponse(
                UUID.randomUUID(),
                name,
                description,
                new BigDecimal(price),
                CREATED_AT,
                UPDATED_AT
        );
    }

    private static String requestJson(String name, String description, String price) {
        return "{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"price\":" + price + "}";
    }
}
