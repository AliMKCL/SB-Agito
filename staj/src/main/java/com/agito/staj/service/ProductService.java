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
    public List<ProductDto> findAll() {
        return ProductMapper.ListProductEntityToDto((productRepository.findAll()));
    }

    public ProductDto find(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("No product with code: " + code));
        return ProductMapper.ProductEntityToDto(product);

        /*
        if (product.isPresent()){
            return productMapper.ProductEntityToDto(product.get());
        }
        else {
            System.out.println("Product not found");
            return null;
        }

         */

    }

    public boolean editProduct(String code, ProductDto productDto) {
        Product productV1 = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("No product with code: " + code)
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

    public void deleteProduct(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("No product with code: " + code)
                );
        productRepository.delete(product);
    }
}
