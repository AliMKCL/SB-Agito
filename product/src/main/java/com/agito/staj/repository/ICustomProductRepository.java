package com.agito.staj.repository;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Product;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ICustomProductRepository {

    public List<Product> findAllByCriteria(
            SearchProductDto searchProductDto
    );
}

