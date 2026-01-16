package co.istad.springdatajpa.controller;

import co.istad.springdatajpa.dto.ProductResponse;
import co.istad.springdatajpa.dto.ProductCreateRequest;
import co.istad.springdatajpa.dto.ProductPatchRequest;
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
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
    void createProduct_zeroPrice_returns400() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(JSON)
                        .content("{\"name\":\"Mouse\",\"description\":\"Wireless\",\"price\":0.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void getProduct_success_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                id,
                "Chair",
                "Office",
                new BigDecimal("89.99"),
                null,
                CREATED_AT,
                UPDATED_AT
        );
        when(productService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Chair"));
    }

    @Test
    void getProduct_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.findById(id)).thenThrow(new ResourceNotFoundException("Product not found: " + id));

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void listProducts_returnsPage() throws Exception {
        ProductResponse response = newResponse("Keyboard", "Mechanical", "99.99");
        Page<ProductResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(productService.findAll(any(Pageable.class), eq(null))).thenReturn(page);

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
    void listProducts_capturesPageableAndSort() throws Exception {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Page<ProductResponse> page = new PageImpl<>(List.of(), PageRequest.of(2, 5), 0);
        when(productService.findAll(captor.capture(), eq(null))).thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(2))
                .andExpect(jsonPath("$.page.size").value(5));

        Pageable pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("name")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("name").isAscending()).isTrue();
    }

    @Test
    void listProducts_sortByNameAsc_ordersContent() throws Exception {
        ProductResponse first = newResponse("Alpha", "A", "1.00");
        ProductResponse second = newResponse("Beta", "B", "2.00");
        Page<ProductResponse> page = new PageImpl<>(List.of(first, second), PageRequest.of(0, 20), 2);
        when(productService.findAll(any(Pageable.class), eq(null))).thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alpha"))
                .andExpect(jsonPath("$.content[1].name").value("Beta"));
    }

    @Test
    void listProducts_outOfRangePage_returnsStablePage() throws Exception {
        when(productService.findAll(any(Pageable.class), eq(null)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    return new PageImpl<>(List.of(), pageable, 0);
                });

        mockMvc.perform(get("/products")
                        .param("page", "999")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page.number").value(999))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
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
    void listProducts_withCategoryFilter_returnsPage() throws Exception {
        UUID categoryId = UUID.randomUUID();
        ProductResponse response = newResponse("Keyboard", "Mechanical", "99.99");
        Page<ProductResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(productService.findAll(any(Pageable.class), eq(categoryId))).thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("categoryId", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void listProducts_categoryNotFound_returns404() throws Exception {
        UUID categoryId = UUID.randomUUID();
        when(productService.findAll(any(Pageable.class), eq(categoryId)))
                .thenThrow(new ResourceNotFoundException("Category not found: " + categoryId));

        mockMvc.perform(get("/products")
                        .param("categoryId", categoryId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void listProducts_unexpectedError_returns500() throws Exception {
        when(productService.findAll(any(Pageable.class), eq(null))).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error occurred"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.message").value(not(containsString("Exception"))))
                .andExpect(jsonPath("$.message").value(not(containsString("StackTrace"))))
                .andExpect(jsonPath("$.message").value(not(containsString("at "))))
                .andExpect(jsonPath("$.message").value(not(containsString("org."))))
                .andExpect(jsonPath("$.message").value(not(containsString("com."))))
                .andExpect(jsonPath("$.message").value(not(containsString("SELECT"))))
                .andExpect(jsonPath("$.message").value(not(containsString("INSERT"))))
                .andExpect(jsonPath("$.message").value(not(containsString("UPDATE"))))
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("StackTrace"))))
                .andExpect(content().string(not(containsString("at "))))
                .andExpect(content().string(not(containsString("org."))))
                .andExpect(content().string(not(containsString("com."))))
                .andExpect(content().string(not(containsString("SELECT"))))
                .andExpect(content().string(not(containsString("INSERT"))))
                .andExpect(content().string(not(containsString("UPDATE"))));
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
                null,
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

    @Test
    void updateProduct_zeroPrice_returns400() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/products/{id}", id)
                        .contentType(JSON)
                        .content("{\"name\":\"Monitor\",\"description\":\"4K\",\"price\":0.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void patchProduct_success_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                id,
                "Desk",
                "Standing desk",
                new BigDecimal("399.00"),
                null,
                CREATED_AT,
                UPDATED_AT
        );
        when(productService.patch(eq(id), any(ProductPatchRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/products/{id}", id)
                        .contentType(JSON)
                        .content("{\"name\":\"Desk\",\"price\":399.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Desk"))
                .andExpect(jsonPath("$.price").value(399.00));
    }

    @Test
    void patchProduct_emptyPayload_returns400() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/products/{id}", id)
                        .contentType(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void patchProduct_invalidPrice_returns400() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/products/{id}", id)
                        .contentType(JSON)
                        .content("{\"price\":0.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void patchProduct_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.patch(eq(id), any(ProductPatchRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found: " + id));

        mockMvc.perform(patch("/products/{id}", id)
                        .contentType(JSON)
                        .content("{\"name\":\"Desk\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteProduct_success_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Product not found: " + id))
                .when(productService)
                .delete(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    private static ProductResponse newResponse(String name, String description, String price) {
        return new ProductResponse(
                UUID.randomUUID(),
                name,
                description,
                new BigDecimal(price),
                null,
                CREATED_AT,
                UPDATED_AT
        );
    }

    private static String requestJson(String name, String description, String price) {
        return "{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"price\":" + price + "}";
    }
}

