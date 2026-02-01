package co.istad.springdatajpa.initialize;

import java.math.BigDecimal;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.entity.ProductVariant;
import co.istad.springdatajpa.entity.AttributeDefinition;
import co.istad.springdatajpa.entity.AttributeDataType;
import co.istad.springdatajpa.entity.AttributeScope;
import co.istad.springdatajpa.entity.ProductAttributeValue;
import co.istad.springdatajpa.entity.ProductAttributeValueId;
import co.istad.springdatajpa.entity.VariantAttributeValue;
import co.istad.springdatajpa.entity.VariantAttributeValueId;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import co.istad.springdatajpa.repository.ProductVariantRepository;
import co.istad.springdatajpa.repository.AttributeDefinitionRepository;
import co.istad.springdatajpa.repository.ProductAttributeValueRepository;
import co.istad.springdatajpa.repository.VariantAttributeValueRepository;

@Component
@Profile({"dev","local"})
public class DataInitialization implements ApplicationRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final boolean seedEnabled;
    private final boolean seedAttributesEnabled;
    private final boolean seedVariantsEnabled;
    private final boolean seedCategoryHierarchyEnabled;

    // Required Args Constructor
    public DataInitialization(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            AttributeDefinitionRepository attributeDefinitionRepository,
            ProductAttributeValueRepository productAttributeValueRepository,
            VariantAttributeValueRepository variantAttributeValueRepository,
            @Value("${app.seed.enabled:true}") boolean seedEnabled,
            @Value("${app.seed.attributes.enabled:true}") boolean seedAttributesEnabled,
            @Value("${app.seed.variants.enabled:true}") boolean seedVariantsEnabled,
            @Value("${app.seed.categoryHierarchy.enabled:true}") boolean seedCategoryHierarchyEnabled
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.variantAttributeValueRepository = variantAttributeValueRepository;
        this.seedEnabled = seedEnabled;
        this.seedAttributesEnabled = seedAttributesEnabled;
        this.seedVariantsEnabled = seedVariantsEnabled;
        this.seedCategoryHierarchyEnabled = seedCategoryHierarchyEnabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            return;
        }
        Map<String, Category> categories = seedCategories();
        seedProducts(categories);
        seedAttributesAndVariants();
    }

    private void seedProducts(Map<String, Category> categories) {
        if (productRepository.count() > 0) {
            return;
        }
        List<Product> products = List.of(
                newProduct("Acer Aspire 5", "15.6-inch laptop, Core i5, 8GB RAM, 512GB SSD", "579.00", categories.get("Laptops")),
                newProduct("ASUS VivoBook 14", "14-inch laptop, Ryzen 5, 8GB RAM, 512GB SSD", "629.00", categories.get("Laptops")),
                newProduct("Dell XPS 13", "13.4-inch laptop, Core i7, 16GB RAM, 1TB SSD", "1399.00", categories.get("Business Laptops")),
                newProduct("Lenovo ThinkPad E14", "14-inch laptop, Core i5, 16GB RAM, 512GB SSD", "899.00", categories.get("Business Laptops")),
                newProduct("HP Omen 16", "Gaming laptop, RTX 4060, 16GB RAM, 1TB SSD", "1499.00", categories.get("Gaming Laptops")),
                newProduct("ASUS ROG Strix G16", "Gaming laptop, RTX 4070, 16GB RAM, 1TB SSD", "1899.00", categories.get("Gaming Laptops")),
                newProduct("Microsoft Surface Pro 9", "2-in-1 tablet, 13-inch, 8GB RAM, 256GB SSD", "1099.00", categories.get("2-in-1 & Tablets")),
                newProduct("Dell Inspiron AIO 24", "All-in-one desktop, Core i5, 16GB RAM, 512GB SSD", "999.00", categories.get("All-in-One (AIO)")),
                newProduct("Custom Gaming PC", "Ryzen 7, RTX 4070, 32GB RAM, 1TB NVMe SSD", "2199.00", categories.get("Custom PC Builder")),
                newProduct("HP ProDesk Tower", "Desktop PC, Core i5, 8GB RAM, 256GB SSD", "699.00", categories.get("Desktops")),
                newProduct("Intel Core i7-14700K", "Desktop CPU, 20-core processor", "389.00", categories.get("CPU (Intel / AMD)")),
                newProduct("AMD Ryzen 7 7800X3D", "Desktop CPU, gaming optimized", "429.00", categories.get("CPU (Intel / AMD)")),
                newProduct("ASUS B650 Motherboard", "AMD AM5 motherboard, Wi-Fi 6", "189.00", categories.get("Motherboards")),
                newProduct("Corsair 32GB DDR5", "DDR5-6000 RAM kit", "139.00", categories.get("RAM")),
                newProduct("NVIDIA RTX 4070 Super", "12GB GDDR6X graphics card", "699.00", categories.get("Graphics Cards (GPU)")),
                newProduct("Samsung 980 Pro 1TB", "NVMe M.2 SSD", "99.00", categories.get("Storage (SSD / HDD / M.2)")),
                newProduct("Seagate 2TB HDD", "7200RPM SATA hard drive", "59.00", categories.get("Storage (SSD / HDD / M.2)")),
                newProduct("Corsair RM750e", "750W 80 Plus Gold PSU", "119.00", categories.get("Power Supplies (PSU)")),
                newProduct("NZXT H5 Flow", "ATX PC case with airflow design", "89.00", categories.get("PC Cases")),
                newProduct("Noctua NH-D15", "Air CPU cooler", "109.00", categories.get("Cooling (Air / Liquid)")),
                newProduct("Arctic P12 Fans (3-pack)", "120mm case fans", "24.00", categories.get("Case Fans")),
                newProduct("LG 27-inch IPS Monitor", "27-inch 1080p IPS monitor", "179.00", categories.get("Monitors")),
                newProduct("ASUS TUF 27-inch 165Hz", "Gaming monitor 165Hz", "259.00", categories.get("Gaming Monitors")),
                newProduct("Keychron K2", "Wireless mechanical keyboard", "89.00", categories.get("Keyboards")),
                newProduct("Logitech MX Master 3S", "Wireless productivity mouse", "99.00", categories.get("Mice")),
                newProduct("HyperX Cloud II", "Gaming headset", "79.00", categories.get("Headsets")),
                newProduct("Creative Pebble 2.0", "Desktop speakers", "29.00", categories.get("Speakers")),
                newProduct("Logitech C920", "Full HD webcam", "69.00", categories.get("Webcams")),
                newProduct("Blue Yeti", "USB microphone", "119.00", categories.get("Microphones")),
                newProduct("Brother Laser Printer", "Monochrome laser printer", "149.00", categories.get("Printers")),
                newProduct("TP-Link AX3000 Router", "Wi-Fi 6 router", "89.00", categories.get("Routers")),
                newProduct("TP-Link 8-Port Switch", "Gigabit network switch", "29.00", categories.get("Network Switches")),
                newProduct("Ubiquiti Access Point", "Wi-Fi access point", "129.00", categories.get("Access Points")),
                newProduct("HDMI 2.1 Cable", "High-speed HDMI cable", "12.00", categories.get("Cables & Adapters")),
                newProduct("USB-C Docking Station", "Dock with HDMI and USB ports", "149.00", categories.get("Docking Stations / USB Hubs")),
                newProduct("External SSD 1TB", "Portable USB-C SSD", "109.00", categories.get("External Storage")),
                newProduct("APC 1000VA UPS", "Battery backup UPS", "169.00", categories.get("UPS")),
                newProduct("Surge Protector", "8-outlet surge protection", "19.00", categories.get("Power Strips / Surge Protectors"))
        );
        productRepository.saveAll(products);
    }

    private void seedAttributesAndVariants() {
        if (seedAttributesEnabled && attributeDefinitionRepository.count() == 0) {
            AttributeDefinition brand = newAttribute("Brand", AttributeDataType.STRING, AttributeScope.BOTH, true);
            AttributeDefinition material = newAttribute("Material", AttributeDataType.STRING, AttributeScope.PRODUCT, true);
            AttributeDefinition color = newAttribute("Color", AttributeDataType.STRING, AttributeScope.VARIANT, true);
            AttributeDefinition weight = newAttribute("WeightKg", AttributeDataType.NUMBER, AttributeScope.PRODUCT, false);
            attributeDefinitionRepository.saveAll(List.of(brand, material, color, weight));
        }

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return;
        }

        Product first = products.get(0);
        Product second = products.size() > 1 ? products.get(1) : null;

        if (seedVariantsEnabled && productVariantRepository.count() == 0) {
            ProductVariant v1 = newVariant(first, "SKU-" + shortId(), "799.00", 10);
            ProductVariant v2 = newVariant(first, "SKU-" + shortId(), "829.00", 5);
            productVariantRepository.saveAll(List.of(v1, v2));

            if (second != null) {
                ProductVariant v3 = newVariant(second, "SKU-" + shortId(), "99.00", 25);
                productVariantRepository.save(v3);
            }
        }

        List<AttributeDefinition> defs = attributeDefinitionRepository.findAll();
        AttributeDefinition brand = defs.stream().filter(d -> d.getName().equals("Brand")).findFirst().orElse(null);
        AttributeDefinition material = defs.stream().filter(d -> d.getName().equals("Material")).findFirst().orElse(null);
        AttributeDefinition color = defs.stream().filter(d -> d.getName().equals("Color")).findFirst().orElse(null);
        AttributeDefinition weight = defs.stream().filter(d -> d.getName().equals("WeightKg")).findFirst().orElse(null);

        if (seedAttributesEnabled && productAttributeValueRepository.count() == 0 && brand != null && material != null && weight != null) {
            productAttributeValueRepository.saveAll(List.of(
                    newProductAttr(first, brand, "Apex", null, null),
                    newProductAttr(first, material, "Aluminum", null, null),
                    newProductAttr(first, weight, null, new BigDecimal("1.8"), null)
            ));
        }

        if (seedAttributesEnabled && variantAttributeValueRepository.count() == 0 && color != null) {
            List<ProductVariant> variants = productVariantRepository.findAll();
            if (!variants.isEmpty()) {
                VariantAttributeValue value = newVariantAttr(variants.get(0), color, "Black", null, null);
                variantAttributeValueRepository.save(value);
            }
        }
    }

    private Map<String, Category> seedCategories() {
        List<Category> categories = List.of(
                newCategory("Laptops", "All laptops and notebooks (Acer, ASUS, Dell, HP, Lenovo...)"),
                newCategory("Gaming Laptops", "High-performance laptops for gaming (RTX series, high refresh rate)"),
                newCategory("Business Laptops", "Office and productivity laptops (ThinkPad, Latitude, EliteBook...)"),
                newCategory("2-in-1 & Tablets", "Convertible laptops and tablets"),
                newCategory("Desktops", "Desktop PCs and towers for home/office use"),
                newCategory("All-in-One (AIO)", "All-in-one desktop computers"),
                newCategory("Workstations", "Professional PCs for design, engineering, and heavy workloads"),
                newCategory("Custom PC Builder", "Custom-built gaming and performance desktop PCs"),
                newCategory("CPU (Intel / AMD)", "Processors for desktop PCs (Intel Core, AMD Ryzen)"),
                newCategory("Motherboards", "Mainboards for Intel and AMD platforms"),
                newCategory("RAM", "Memory modules DDR4/DDR5 for laptops/desktops"),
                newCategory("Graphics Cards (GPU)", "NVIDIA and AMD GPUs for gaming and productivity"),
                newCategory("Storage (SSD / HDD / M.2)", "NVMe SSD, SATA SSD, HDD storage solutions"),
                newCategory("Power Supplies (PSU)", "Power supplies for desktops (80 Plus Bronze/Gold/Platinum)"),
                newCategory("PC Cases", "Computer cases and chassis"),
                newCategory("Cooling (Air / Liquid)", "CPU coolers and liquid cooling systems"),
                newCategory("Case Fans", "PC fans for airflow and cooling"),
                newCategory("Monitors", "Office and general-purpose monitors"),
                newCategory("Gaming Monitors", "High refresh rate monitors (144Hz/165Hz/240Hz)"),
                newCategory("Monitor Accessories", "Monitor arm, cables, adapters, stands"),
                newCategory("Keyboards", "Office and mechanical keyboards"),
                newCategory("Mice", "Wireless and gaming mice"),
                newCategory("Headsets", "Gaming and office headsets"),
                newCategory("Speakers", "Desktop and portable speakers"),
                newCategory("Webcams", "Webcams for meetings and streaming"),
                newCategory("Microphones", "Microphones for recording and streaming"),
                newCategory("Printers", "Inkjet and laser printers"),
                newCategory("Scanners", "Document scanners"),
                newCategory("Ink & Toner", "Printer ink cartridges and toner"),
                newCategory("Networking", "Routers, switches, Wi-Fi access points"),
                newCategory("Routers", "Wi-Fi routers and mesh systems"),
                newCategory("Network Switches", "LAN switches for home and office networking"),
                newCategory("Access Points", "Wi-Fi access points for extended coverage"),
                newCategory("Cables & Adapters", "LAN cables, HDMI, DisplayPort, USB cables and adapters"),
                newCategory("Laptop Accessories", "Laptop bags, chargers, hubs and docking stations"),
                newCategory("Docking Stations / USB Hubs", "USB hubs, docking stations, Type-C adapters"),
                newCategory("External Storage", "External SSD/HDD and USB flash drives"),
                newCategory("UPS", "Power backup UPS devices for desktops and office"),
                newCategory("Power Strips / Surge Protectors", "Electrical protection for devices")
        );
        Map<String, Category> persisted = new HashMap<>();
        for (Category category : categories) {
            Category saved = categoryRepository.findByName(category.getName())
                    .orElseGet(() -> categoryRepository.save(category));
            persisted.put(saved.getName(), saved);
        }
        if (seedCategoryHierarchyEnabled) {
            Category laptops = persisted.get("Laptops");
            Category gaming = persisted.get("Gaming Laptops");
            Category business = persisted.get("Business Laptops");
            if (laptops != null) {
                if (gaming != null) {
                    gaming.setParent(laptops);
                    gaming.setSortOrder(1);
                    categoryRepository.save(gaming);
                }
                if (business != null) {
                    business.setParent(laptops);
                    business.setSortOrder(2);
                    categoryRepository.save(business);
                }
            }
        }
        return persisted;
    }

    private Category newCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Product newProduct(String name, String description, String price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setCategory(category);
        return product;
    }

    private AttributeDefinition newAttribute(String name, AttributeDataType type, AttributeScope scope, boolean filterable) {
        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(name);
        definition.setDataType(type);
        definition.setScope(scope);
        definition.setFilterable(filterable);
        return definition;
    }

    private ProductVariant newVariant(Product product, String sku, String price, int stock) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(sku);
        variant.setPrice(new BigDecimal(price));
        variant.setStock(stock);
        return variant;
    }

    private ProductAttributeValue newProductAttr(Product product,
                                                 AttributeDefinition attribute,
                                                 String valueString,
                                                 BigDecimal valueNumber,
                                                 Boolean valueBoolean) {
        ProductAttributeValue value = new ProductAttributeValue();
        value.setId(new ProductAttributeValueId(product.getId(), attribute.getId()));
        value.setProduct(product);
        value.setAttribute(attribute);
        value.setValueString(valueString);
        value.setValueNumber(valueNumber);
        value.setValueBoolean(valueBoolean);
        return value;
    }

    private VariantAttributeValue newVariantAttr(ProductVariant variant,
                                                 AttributeDefinition attribute,
                                                 String valueString,
                                                 BigDecimal valueNumber,
                                                 Boolean valueBoolean) {
        VariantAttributeValue value = new VariantAttributeValue();
        value.setId(new VariantAttributeValueId(variant.getId(), attribute.getId()));
        value.setVariant(variant);
        value.setAttribute(attribute);
        value.setValueString(valueString);
        value.setValueNumber(valueNumber);
        value.setValueBoolean(valueBoolean);
        return value;
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
