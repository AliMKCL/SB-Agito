package com.agito.staj.service;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Product;
import com.agito.staj.exception.DuplicateProductException;
import com.agito.staj.exception.ProductNotFoundException;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     *
     * @param productDto
     * @return the created product.
     * Creates a product inside the database from the input ProductDto object.
     */
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.ProductDtoToEntity(productDto);

        Optional<Product> exists = productRepository.findByCode(product.getCode());

        if (exists.isPresent()){
            throw new DuplicateProductException("There is already a product with code "+ product.getCode());
        }
        else {
            productRepository.save(product);
            return ProductMapper.ProductEntityToDto(product);
        }
    }

    // kod, name, category ile arama
    // criteria query (criteria builder)

    /**
     *
     * @return all products inside the database.
     */
    public List<ProductDto> findAll() {
        return ProductMapper.ListProductEntityToDto((productRepository.findAll()));
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
    public boolean editProduct(ProductDto productDto) {
        Product productV1 = productRepository.findByCode(productDto.getCode())
                .orElseThrow(() -> new ProductNotFoundException("No product with code: " + productDto.getCode())
        );
        Product product = ProductMapper.ProductDtoToEntity(productDto);

        if (productV1.getName().equals(product.getName())){
            productV1.setCode(product.getCode());
            productV1.setCategory(product.getCategory());
            productV1.setStock(product.getStock());
            productV1.setPrice(product.getPrice());
            productRepository.save(productV1);
            return true;
        }
        return false;
    }

    // CHANGE EDIT TO ONLY TAKE IN A DTO AND EDIT IF ITEM WITH ITS CODE EXISTS.
    // RETURN THE ??


    /**
     *
     * @param code
     * Deletes the product from the database with the input code.
     */
    public void deleteProduct(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("No product with code: " + code)
                );
        productRepository.delete(product);
    }
}
