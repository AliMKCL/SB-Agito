package com.agito.staj.service.impl;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Product;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.repository.ProductRepository;
import com.agito.staj.service.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//dto entity map service içinde
@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService{

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // kod, name, category ile arama
    // criteria query (criteria builder)
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product find(String name) {
        Optional<Product> product = productRepository.findByName(name);

        if (product.isPresent()){
            return product.get();
        }
        else {
            System.out.println("Product not found");
            return null;
        }

    }

    @Override
    public void editProduct(String name, Product product) {
        Optional<Product> productV1 = productRepository.findByName(name);

        if (productV1.isEmpty()){
            System.out.println("No product found"); // Err handling
        }
        else {
            if (productV1.get().getName().equals(product.getName())){
                productV1.get().setCategory(product.getCategory());
                productV1.get().setStock(product.getStock());
                productV1.get().setPrice(product.getPrice());
                productRepository.save(productV1.get());
            }
        }

    }

    @Override
    public Product deleteProduct(String name) {
        Optional<Product> product = productRepository.findByName(name); //findbycode olacak
        if (product.isPresent()){
            productRepository.delete(product.get());
            return product.get();
        }
        else{
            System.out.println("Product does not exist"); // Err handling
            return null;
        }
    }
}
