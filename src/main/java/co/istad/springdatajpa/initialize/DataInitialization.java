package co.istad.springdatajpa.initialize;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.repository.ProductRepository;

@Component
@Profile("dev")
public class DataInitialization {
    private final ProductRepository productRepository;

    // Required Args Constructor
    public DataInitialization(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    void init() {
        List<Product> products = List.of(
                Product.builder()
                        .name("Notebook")
                        .description("A5 dot-grid notebook")
                        .price(new BigDecimal("4.99"))
                        .build(),
                Product.builder()
                        .name("Pen")
                        .description("Black gel pen")
                        .price(new BigDecimal("1.49"))
                        .build(),
                Product.builder()
                        .name("Backpack")
                        .description("Water-resistant daypack")
                        .price(new BigDecimal("29.90"))
                        .build()
        );
        productRepository.saveAll(products);
    }
}
