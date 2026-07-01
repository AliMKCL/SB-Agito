package com.agito.staj.service;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Product;
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

    private final ProductMapper productMapper;

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.ProductDtoToEntity(productDto);
        productRepository.save(product);
        return productMapper.ProductEntityToDto(product);
    }

    // kod, name, category ile arama
    // criteria query (criteria builder)
    public List<ProductDto> findAll() {
        return productMapper.ListProductEntityToDto((productRepository.findAll()));
    }

    public ProductDto find(String code) {
        Optional<Product> product = productRepository.findByCode(code);

        if (product.isPresent()){
            return productMapper.ProductEntityToDto(product.get());
        }
        else {
            System.out.println("Product not found");
            return null;
        }

    }

    public boolean editProduct(String code, ProductDto productDto) {
        Optional<Product> productV1 = productRepository.findByCode(code);
        Product product = productMapper.ProductDtoToEntity(productDto);

        if (productV1.isEmpty()){
            return false;
        }
        else {
            if (productV1.get().getName().equals(product.getName())){
                productV1.get().setCode(product.getCode());
                productV1.get().setCategory(product.getCategory());
                productV1.get().setStock(product.getStock());
                productV1.get().setPrice(product.getPrice());
                productRepository.save(productV1.get());
                return true;
            }
        }
        return false;

    }

    public boolean deleteProduct(String code) {
        Optional<Product> product = productRepository.findByCode(code); //findbycode olacak
        if (product.isPresent()){
            productRepository.delete(product.get());
            return true;
        }
        else{
            System.out.println("Product does not exist"); // Err handling
            return false;
        }
    }
}
