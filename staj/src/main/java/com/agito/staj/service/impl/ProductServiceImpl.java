package com.agito.staj.service.impl;

import com.agito.staj.entity.Product;
import com.agito.staj.repository.ProductRepository;
import com.agito.staj.service.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void editProduct(Product product) {

    }

    @Override
    public Product deleteProduct(String name) {
        return null;
    }
}
