package com.agito.staj.service;

import com.agito.staj.entity.Product;

import java.util.List;

public interface IProductService {

    Product createProduct(Product product);

    List<Product> findAll();

    void editProduct(Product product);

    Product deleteProduct(String name);
}
