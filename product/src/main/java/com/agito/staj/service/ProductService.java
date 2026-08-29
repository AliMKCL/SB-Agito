package com.agito.staj.service;

import com.agito.staj.dto.ProdCreateCommDto;
import com.agito.staj.dto.ProdDeleteCommDto;
import com.agito.staj.dto.ProdEditCommDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.IProductRepository;
import com.agito.staj.repository.impl.CustomProductRepository;
import com.agito.staj.validator.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductService {

    private final IProductRepository productRepository;
    private final CustomProductRepository customProductRepository;
    private final CategoryRepository categoryRepository;
    private final StreamBridge streamBridge;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public ProductService(IProductRepository productRepository,
                          CustomProductRepository customProductRepository,
                          CategoryRepository categoryRepository,
                          StreamBridge streamBridge) {
        this.productRepository = productRepository;
        this.customProductRepository = customProductRepository;
        this.categoryRepository = categoryRepository;
        this.streamBridge = streamBridge;
    }

    @Cacheable(value = "categories", key = "#categoryId + '_' + (T(org.springframework.context.i18n.LocaleContextHolder).getLocale() != null ? T(org.springframework.context.i18n.LocaleContextHolder).getLocale().getLanguage() : 'en')")
    public Category getCategoryById(Integer categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.orElse(null);
    }

    /**
     * @param productDto
     * @return the created product.
     * Creates a product inside the database from the input ProductDto object.
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createProduct(ProductDto productDto) {
        Category category = ProductValidator.validateCategoryExists(
                Optional.ofNullable(getCategoryById(productDto.getCategoryId())),
                productDto.getCategoryId()
        );

        ProductValidator.validateCategoryIsLeaf(category);

        ProductValidator.validateProductNotDuplicate(
                productRepository.findByCode(productDto.getCode()).isPresent(),
                productDto.getCode()
        );

        Product product = ProductMapper.ProductDtoToEntity(productDto, category);
        Locale currentLocale = LocaleContextHolder.getLocale();
        String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
        product.addOrUpdateTranslation(activeLang, productDto.getName());

        productRepository.save(product);
        sendCommunication(product);
        return ProductMapper.ProductEntityToDto(product);
    }

    private void sendCommunication(Product product) {
        var prodCreateCommDto = new ProdCreateCommDto(product.getCode(), product.getPrice());
        log.info("***** Sending Communication request for the details: {}", prodCreateCommDto);
        var result = streamBridge.send("registerNewProduct-out-0", prodCreateCommDto);
        log.info("***** Is the Communication request successfully triggered ? : {}", result);
    }

    /**
     * @return all products inside the database matching criteria.
     */
    public List<ProductDto> findAll(SearchProductDto searchProductDto) {
        return ProductMapper.ListProductEntityToDto(customProductRepository.findAllByCriteria(searchProductDto));
    }

    @Cacheable(value = "products", key = "#code + '_' + (T(org.springframework.context.i18n.LocaleContextHolder).getLocale() != null ? T(org.springframework.context.i18n.LocaleContextHolder).getLocale().getLanguage() : 'en')")
    public ProductDto find(String code) {
        Product product = ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        return ProductMapper.ProductEntityToDto(product);
    }

    /**
     * @param productDto
     */
    @CacheEvict(value = "products", allEntries = true)
    public void editProduct(ProductDto productDto) {
        ProductValidator.validateProductExists(
                productRepository.findByCode(productDto.getCode()),
                productDto.getCode()
        );

        Category category = getCategoryById(productDto.getCategoryId());
        ProductValidator.validateCategoryLeafForEdit(
                Optional.ofNullable(category),
                productDto.getCategoryId()
        );

        Product product = productRepository.findByCode(productDto.getCode()).get();
        Locale currentLocale = LocaleContextHolder.getLocale();
        String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
        product.addOrUpdateTranslation(activeLang, productDto.getName());

        product.setCode(productDto.getCode());
        product.setCategory(category);
        product.setPrice(productDto.getPrice());
        product.setCommCompleted(false);
        productRepository.save(product);

        sendEditCommunication(product);
    }

    private void sendEditCommunication(Product product) {
        var prodEditCommDto = new ProdEditCommDto(product.getCode(), product.getPrice());
        log.info("Sending Edit Communication request for the details: {}", prodEditCommDto);
        var result = streamBridge.send("registerEditProduct-out-0", prodEditCommDto);
        log.info("Is the Edit Communication request successfully triggered ? : {}", result);
    }

    /**
     * @param code Deletes the product from the database with the input code.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(String code) {
        ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        sendDeleteCommunication(code);
    }

    private void sendDeleteCommunication(String code) {
        var prodDeleteCommDto = new ProdDeleteCommDto(code);
        log.info("Sending delete request for the details: {}", prodDeleteCommDto);
        var result = streamBridge.send("registerDeleteProduct-out-0", prodDeleteCommDto);
        log.info("Is the Delete Communication request successfully triggered ? : {}", result);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProductLocal(String code) {
        Product product = ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        productRepository.delete(product);
    }

    public void updateCommSwitch(String code) {
        Product product = ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        product.setCommCompleted(true);
        productRepository.save(product);
    }

    public List<ProductDto> seedDatabase() {
        List<ProductSeedData> seeds = List.of(
                // Laptops (Category ID: 3)
                new ProductSeedData("L001", "MacBook Pro 14", "MacBook Pro 14", 3, 1999.00),
                new ProductSeedData("L002", "MacBook Air 13", "MacBook Air 13", 3, 1099.00),
                new ProductSeedData("L003", "Dell XPS 13", "Dell XPS 13", 3, 1299.00),
                new ProductSeedData("L004", "ThinkPad X1 Carbon", "ThinkPad X1 Carbon", 3, 1599.00),
                new ProductSeedData("L005", "HP Spectre x360", "HP Spectre x360", 3, 1399.00),
                new ProductSeedData("L006", "Asus ROG Zephyrus", "Asus ROG Zephyrus Oyun Bilgisayarı", 3, 1799.00),
                new ProductSeedData("L007", "Razer Blade 15", "Razer Blade 15", 3, 2399.00),
                new ProductSeedData("L008", "Acer Swift 3", "Acer Swift 3", 3, 699.00),
                new ProductSeedData("L009", "Lenovo Yoga 9i", "Lenovo Yoga 9i", 3, 1499.00),
                new ProductSeedData("L010", "LG Gram 17", "LG Gram 17", 3, 1599.00),
                new ProductSeedData("L011", "Dell Inspiron 15", "Dell Inspiron 15", 3, 649.00),
                new ProductSeedData("L012", "HP Pavilion 14", "HP Pavilion 14", 3, 599.00),
                new ProductSeedData("L013", "Microsoft Surface 5", "Microsoft Surface 5", 3, 999.00),
                new ProductSeedData("L014", "Acer Predator", "Acer Predator", 3, 1499.00),
                new ProductSeedData("L015", "MSI Stealth 15", "MSI Stealth 15", 3, 1899.00),

                // Desktops (Category ID: 4)
                new ProductSeedData("D001", "iMac 24", "iMac 24 Masaüstü", 4, 1299.00),
                new ProductSeedData("D002", "Mac Studio", "Mac Studio", 4, 1999.00),
                new ProductSeedData("D003", "Dell Inspiron 3910", "Dell Inspiron 3910", 4, 599.00),
                new ProductSeedData("D004", "HP Pavilion TP01", "HP Pavilion TP01", 4, 649.00),
                new ProductSeedData("D005", "Lenovo Legion T5", "Lenovo Legion T5", 4, 1199.00),
                new ProductSeedData("D006", "Alienware Aurora", "Alienware Aurora", 4, 2199.00),
                new ProductSeedData("D007", "Asus ROG Strix G15", "Asus ROG Strix G15", 4, 1399.00),
                new ProductSeedData("D008", "Acer Aspire TC", "Acer Aspire TC", 4, 499.00),
                new ProductSeedData("D009", "Mac Pro", "Mac Pro", 4, 5999.00),
                new ProductSeedData("D010", "CyberPowerPC Gamer", "CyberPowerPC Gamer Masaüstü", 4, 999.00),
                new ProductSeedData("D011", "HP Omen 40L", "HP Omen 40L", 4, 1499.00),
                new ProductSeedData("D012", "Dell OptiPlex 7000", "Dell OptiPlex 7000", 4, 899.00),
                new ProductSeedData("D013", "Skytech Archangel", "Skytech Archangel", 4, 1099.00),
                new ProductSeedData("D014", "Lenovo IdeaCentre 5", "Lenovo IdeaCentre 5", 4, 549.00),
                new ProductSeedData("D015", "Corsair One i300", "Corsair One i300", 4, 3599.00)
        );

        List<ProductDto> seededProducts = new ArrayList<>();
        for (ProductSeedData seed : seeds) {
            if (productRepository.findByCode(seed.code).isEmpty()) {
                Category category = getCategoryById(seed.categoryId);
                if (category != null) {
                    Product product = new Product();
                    product.setCode(seed.code);
                    product.setCategory(category);
                    product.setPrice(seed.price);
                    product.addOrUpdateTranslation("en", seed.nameEn);
                    product.addOrUpdateTranslation("tr", seed.nameTr);
                    productRepository.save(product);
                    sendCommunication(product);
                    seededProducts.add(ProductMapper.ProductEntityToDto(product));
                }
            }
        }
        return seededProducts;
    }

    private static class ProductSeedData {
        String code;
        String nameEn;
        String nameTr;
        Integer categoryId;
        Double price;

        ProductSeedData(String code, String nameEn, String nameTr, Integer categoryId, Double price) {
            this.code = code;
            this.nameEn = nameEn;
            this.nameTr = nameTr;
            this.categoryId = categoryId;
            this.price = price;
        }
    }
}
