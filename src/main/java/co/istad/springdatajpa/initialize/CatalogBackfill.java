package co.istad.springdatajpa.initialize;

import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"dev", "local"})
public class CatalogBackfill implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final boolean backfillEnabled;

    public CatalogBackfill(
            ProductRepository productRepository,
            @Value("${app.catalog.backfill.enabled:true}") boolean backfillEnabled
    ) {
        this.productRepository = productRepository;
        this.backfillEnabled = backfillEnabled;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!backfillEnabled) {
            return;
        }
        List<Product> products = productRepository.findAll();
        boolean changed = false;
        for (Product product : products) {
            Category legacy = product.getLegacyCategory();
            if (product.getCategory() == null && legacy != null) {
                product.setCategory(legacy);
                changed = true;
                continue;
            }
            if (product.getCategory() != null && !product.getCategories().contains(product.getCategory())) {
                product.addCategory(product.getCategory());
                changed = true;
            }
        }
        if (changed) {
            productRepository.saveAll(products);
        }
    }
}
