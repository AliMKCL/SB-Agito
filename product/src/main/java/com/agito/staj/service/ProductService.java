package com.agito.staj.service;

import com.agito.staj.dto.ProdCreateCommDto;
import com.agito.staj.dto.ProdDeleteCommDto;
import com.agito.staj.dto.ProdEditCommDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.entity.SearchOperation;
import com.agito.staj.exception.*;
import com.agito.staj.validator.ProductValidator;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.IProductRepository;
import com.agito.staj.repository.impl.CustomProductRepository;
import com.agito.staj.service.client.StockFeignClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final IProductRepository productRepository;

    private final CustomProductRepository customProductRepository;

    private final CategoryRepository categoryRepository;

    private final StockFeignClient stockFeignClient;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final StreamBridge streamBridge;


    @Cacheable(value = "categories", key = "#categoryId")
    public Category getCategoryById(Integer categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.orElse(null);
    }
    /**
     *
     * @param productDto
     * @return the created product.
     * Creates a product inside the database from the input ProductDto object.
     */
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

        productRepository.save(product);
        //stockFeignClient.addStockAuto(product.getCode(), 0, product.getPrice());
        sendCommunication(product);
        return ProductMapper.ProductEntityToDto(product);
    }

    private void sendCommunication(Product product) {
        var prodCreateCommDto = new ProdCreateCommDto(product.getCode(), product.getPrice());
        log.info("***** Sending Communication request for the details: {}", prodCreateCommDto);
        var result = streamBridge.send("registerNewProduct-out-0", prodCreateCommDto); // Sends the data of the created object as an event.
        log.info("***** Is the Communication request successfully triggered ? : {}", result);
    }


    /**
     *
     * @return all products inside the database.
     */
    public List<ProductDto> findAll(SearchProductDto searchProductDto) {
        return ProductMapper.ListProductEntityToDto((customProductRepository.findAllByCriteria(searchProductDto)));
    }

    @Cacheable(value = "products", key = "#code")
    public ProductDto find(String code) {
        Product product = ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        return ProductMapper.ProductEntityToDto(product);
    }


    /**
     *
     * @param productDto
     * @return boolean value describing whether an editing was successful or failed.
     */
    @CacheEvict(value = "products", key="#productDto.getCode()")
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
        product.setName(productDto.getName());
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
     *
     * @param code
     * Deletes the product from the database with the input code.
     */
    @CacheEvict(value = "products", key = "#code")
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

    @CacheEvict(value = "products", key = "#code")
    public void deleteProductLocal(String code) {
        Product product = ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        productRepository.delete(product);
    }

    public void updateCommSwitch(String code){
        Product product = ProductValidator.validateProductExists(
                productRepository.findByCode(code),
                code
        );
        product.setCommCompleted(true);
        productRepository.save(product);
    }

    public List<ProductDto> seedDatabase() {
        List<ProductDto> productsToSeed = List.of(
            // Laptops (Category ID: 3)
            createProductDto("L001", "MacBook Pro 14", 3, 1999.00),
            createProductDto("L002", "MacBook Air 13", 3, 1099.00),
            createProductDto("L003", "Dell XPS 13", 3, 1299.00),
            createProductDto("L004", "ThinkPad X1 Carbon", 3, 1599.00),
            createProductDto("L005", "HP Spectre x360", 3, 1399.00),
            createProductDto("L006", "Asus ROG Zephyrus", 3, 1799.00),
            createProductDto("L007", "Razer Blade 15", 3, 2399.00),
            createProductDto("L008", "Acer Swift 3", 3, 699.00),
            createProductDto("L009", "Lenovo Yoga 9i", 3, 1499.00),
            createProductDto("L010", "LG Gram 17", 3, 1599.00),
            createProductDto("L011", "Dell Inspiron 15", 3, 649.00),
            createProductDto("L012", "HP Pavilion 14", 3, 599.00),
            createProductDto("L013", "Microsoft Surface 5", 3, 999.00),
            createProductDto("L014", "Acer Predator", 3, 1499.00),
            createProductDto("L015", "MSI Stealth 15", 3, 1899.00),

            // Desktops (Category ID: 4)
            createProductDto("D001", "iMac 24", 4, 1299.00),
            createProductDto("D002", "Mac Studio", 4, 1999.00),
            createProductDto("D003", "Dell Inspiron 3910", 4, 599.00),
            createProductDto("D004", "HP Pavilion TP01", 4, 649.00),
            createProductDto("D005", "Lenovo Legion T5", 4, 1199.00),
            createProductDto("D006", "Alienware Aurora", 4, 2199.00),
            createProductDto("D007", "Asus ROG Strix G15", 4, 1399.00),
            createProductDto("D008", "Acer Aspire TC", 4, 499.00),
            createProductDto("D009", "Mac Pro", 4, 5999.00),
            createProductDto("D010", "CyberPowerPC Gamer", 4, 999.00),
            createProductDto("D011", "HP Omen 40L", 4, 1499.00),
            createProductDto("D012", "Dell OptiPlex 7000", 4, 899.00),
            createProductDto("D013", "Skytech Archangel", 4, 1099.00),
            createProductDto("D014", "Lenovo IdeaCentre 5", 4, 549.00),
            createProductDto("D015", "Corsair One i300", 4, 3599.00)
        );

        List<ProductDto> seededProducts = new java.util.ArrayList<>();
        for (ProductDto dto : productsToSeed) {
            if (productRepository.findByCode(dto.getCode()).isEmpty()) {
                seededProducts.add(createProduct(dto));
            }
        }
        return seededProducts;
    }

    private ProductDto createProductDto(String code, String name, Integer categoryId, Double price) {
        ProductDto dto = new ProductDto();
        dto.setCode(code);
        dto.setName(name);
        dto.setCategoryId(categoryId);
        dto.setPrice(price);
        return dto;
    }
}
