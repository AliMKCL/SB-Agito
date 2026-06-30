package com.agito.staj.service;

import com.agito.staj.entity.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    Product createProduct(Product product);

    List<Product> findAll();

    Product find(String name);

    void editProduct(String name, Product product);

    Product deleteProduct(String name);
}
