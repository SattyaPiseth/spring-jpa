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

import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;

@Component
@Profile({"dev","local"})
public class DataInitialization implements ApplicationRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final boolean seedEnabled;

    // Required Args Constructor
    public DataInitialization(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            @Value("${app.seed.enabled:true}") boolean seedEnabled
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            return;
        }
        Map<String, Category> categories = seedCategories();
        seedProducts(categories);
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
}

