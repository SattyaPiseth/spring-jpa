package co.istad.springdatajpa.initialize;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.repository.ProductRepository;

@Component
@Profile("dev")
public class DataInitialization implements ApplicationRunner {
    private final ProductRepository productRepository;

    // Required Args Constructor
    public DataInitialization(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Product> products = List.of(
                newProduct("Notebook", "A5 dot-grid notebook", "4.99"),
                newProduct("Pen", "Black gel pen", "1.49"),
                newProduct("Backpack", "Water-resistant daypack", "29.90")
        );
        productRepository.saveAll(products);
    }

    private Product newProduct(String name, String description, String price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        return product;
    }
}
