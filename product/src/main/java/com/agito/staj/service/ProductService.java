package com.agito.staj.service;

import com.agito.staj.dto.ProdCreateCommDto;
import com.agito.staj.dto.ProdDeleteCommDto;
import com.agito.staj.dto.ProdEditCommDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.exception.*;
import com.agito.staj.validator.ProductValidator;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.IProductRepository;
import com.agito.staj.repository.impl.CustomProductRepository;
import com.agito.staj.service.client.StockFeignClient;
import lombok.AllArgsConstructor;
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

        // Category veritabanından çekmek yerine redis cacheden alsın

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
}
