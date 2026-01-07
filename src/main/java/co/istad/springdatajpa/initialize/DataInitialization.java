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
                newProduct("Notebook A5", "A5 dot-grid notebook", "4.99"),
                newProduct("Notebook A4", "A4 ruled notebook", "7.99"),
                newProduct("Pen Black", "Black gel pen", "1.49"),
                newProduct("Pen Blue", "Blue gel pen", "1.49"),
                newProduct("Pencil HB", "HB wooden pencil", "0.99"),
                newProduct("Mechanical Pencil", "0.5mm mechanical pencil", "2.49"),
                newProduct("Eraser", "Soft rubber eraser", "0.59"),
                newProduct("Sharpener", "Metal pencil sharpener", "0.79"),
                newProduct("Highlighter Yellow", "Yellow highlighter pen", "1.29"),
                newProduct("Highlighter Pink", "Pink highlighter pen", "1.29"),
                newProduct("Marker Permanent", "Black permanent marker", "1.99"),
                newProduct("Sticky Notes", "3x3 sticky notes pack", "2.99"),
                newProduct("Paper Clips", "Pack of 100 paper clips", "1.89"),
                newProduct("Binder Clips", "Pack of 12 binder clips", "2.49"),
                newProduct("Stapler", "Standard office stapler", "5.99"),
                newProduct("Staples", "Staples refill pack", "1.99"),
                newProduct("Scissors", "Office scissors", "4.49"),
                newProduct("Ruler 30cm", "30cm plastic ruler", "1.19"),
                newProduct("Calculator", "Basic desk calculator", "9.99"),
                newProduct("Desk Organizer", "Plastic desk organizer", "6.99"),
                newProduct("Backpack", "Water-resistant daypack", "29.90"),
                newProduct("Laptop Sleeve", "13-inch laptop sleeve", "12.99"),
                newProduct("USB Flash Drive", "32GB USB 3.0 flash drive", "8.49"),
                newProduct("Mouse Pad", "Non-slip mouse pad", "3.49"),
                newProduct("Wireless Mouse", "2.4GHz wireless mouse", "14.99")
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
