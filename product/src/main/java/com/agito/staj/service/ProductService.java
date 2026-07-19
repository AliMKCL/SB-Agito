package com.agito.staj.service;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.exception.*;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.ProductRepository;
import com.agito.staj.repository.impl.CustomProductRepository;
import com.agito.staj.service.client.StockFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CustomProductRepository customProductRepository;

    private final CategoryRepository categoryRepository;

    private final StockFeignClient stockFeignClient;


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
        stockFeignClient.addStockAuto(product.getCode(), 0, product.getPrice());
        return ProductMapper.ProductEntityToDto(product);
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
        productRepository.save(product);

        // Change the unit sale price of the item inside StockDB as well.
        stockFeignClient.editItem(product.getCode(), product.getPrice());
    }


    /**
     *
     * @param code
     * Deletes the product from the database with the input code.
     */
    public void deleteProduct(String code) {

        Optional<Product> product = productRepository.findByCode(code);
        if (product.isPresent()){
            productRepository.delete(product.get());
            stockFeignClient.deleteItem(code);
        }
        else {
            throw new ProductNotFoundException("No product with code: " + code);
        }

    }
}
