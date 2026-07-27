package com.agito.staj.service;

import com.agito.staj.dto.ProdCreateCommDto;
import com.agito.staj.dto.ProdDeleteCommDto;
import com.agito.staj.dto.ProdEditCommDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.exception.*;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.IProductRepository;
import com.agito.staj.repository.impl.CustomProductRepository;
import com.agito.staj.service.client.StockFeignClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    /**
     *
     * @param productDto
     * @return the created product.
     * Creates a product inside the database from the input ProductDto object.
     */
    public ProductDto createProduct(ProductDto productDto) {

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + productDto.getCategoryId()));

        if (!category.getChildren().isEmpty()) {
            throw new InvalidCategoryException(
                    "Products can only be assigned to leaf categories (end of inheritance line). " +
                            "Category '" + category.getName() + "' is a parent category."
            );
        }

        if (productRepository.findByCode(productDto.getCode()).isPresent()){
            throw new DuplicateProductException("There is already a product with code "+ productDto.getCode());

        }

        // ProductValidator, bu methodun throwladığı errrorlar içi nayrı  bir class

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

    public ProductDto find(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("No product with code: " + code));
        return ProductMapper.ProductEntityToDto(product);
    }


    /**
     *
     * @param productDto
     * @return boolean value describing whether an editing was successful or failed.
     */
    public void editProduct(ProductDto productDto) {

        if (productRepository.findByCode(productDto.getCode()).isEmpty()) {
            throw new ProductNotFoundException("No product with code: " + productDto.getCode());
        };

        if (categoryRepository.findById(productDto.getCategoryId()).isEmpty()){
            throw new InvalidCategoryException("The new category of the item does not exist");
        }

        if (!categoryRepository.findById(productDto.getCategoryId()).get().getChildren().isEmpty()){
            throw new CategoryNotLeafException("The new category of the item is not a leaf category");
        }


        Product product = productRepository.findByCode(productDto.getCode()).get();
        product.setName(productDto.getName());
        product.setCode(productDto.getCode());
        product.setCategory(categoryRepository.findById(productDto.getCategoryId()).get());
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
    public void deleteProduct(String code) {
        Optional<Product> product = productRepository.findByCode(code);
        if (product.isPresent()){
            sendDeleteCommunication(code);
        }
        else {
            throw new ProductNotFoundException("No product with code: " + code);
        }
    }

    private void sendDeleteCommunication(String code) {
        var prodDeleteCommDto = new ProdDeleteCommDto(code);
        log.info("Sending delete request for the details: {}", prodDeleteCommDto);
        var result = streamBridge.send("registerDeleteProduct-out-0", prodDeleteCommDto);
        log.info("Is the Delete Communication request successfully triggered ? : {}", result);
    }

    public void deleteProductLocal(String code) {
        Optional<Product> product = productRepository.findByCode(code);
        if (product.isPresent()){
            productRepository.delete(product.get());
        }
        else {
            throw new ProductNotFoundException("No product with code: " + code);
        }
    }

    public void updateCommSwitch(String code){
        Optional<Product> product = productRepository.findByCode(code);
        if (product.isPresent()){
            product.get().setCommCompleted(true);
            productRepository.save(product.get());
        }
        else {
            throw new ProductNotFoundException("No product with code: " + code);
        }
    }
}
