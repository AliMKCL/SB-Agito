package com.agito.staj.repository.impl;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Product;
import com.agito.staj.repository.ICustomProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomProductRepository implements ICustomProductRepository {

    private final EntityManager entityManager;


    @Override
    public List<Product> findAllByCriteria(SearchProductDto searchProductDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // SELECT FROM product
        Root<Product> root = criteriaQuery.from(Product.class);
        if (searchProductDto.getCode() != null){
            Predicate codePredicate = criteriaBuilder.like(root.get("code"), "%" + searchProductDto.getCode() + "%");
            predicates.add(codePredicate);
        }
        if (searchProductDto.getName() != null){
            Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + searchProductDto.getName() + "%");
            predicates.add(namePredicate);
        }
        if (searchProductDto.getCategory() != null){
            Predicate categoryPredicate = criteriaBuilder.like(root.get("category"), "%" + searchProductDto.getCategory() + "%");
            predicates.add(categoryPredicate);
        }

        criteriaQuery.where(
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );

        TypedQuery<Product> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}

