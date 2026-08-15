package com.agito.staj.repository.impl;

import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.ICustomProductRepository;
import com.agito.staj.repository.IProductRepository;
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

    private final IProductRepository productRepository;

    private final CategoryRepository categoryRepository;


    @Override
    public List<Product> findAllByCriteria(SearchProductDto searchProductDto) {


        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        // SELECT FROM product
        Root<Product> root = criteriaQuery.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchProductDto.getCode() != null){
            Predicate codePredicate = criteriaBuilder.like(root.get("code"), "%" + searchProductDto.getCode() + "%");
            predicates.add(codePredicate);
        }
        if (searchProductDto.getName() != null){
            Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + searchProductDto.getName() + "%");
            predicates.add(namePredicate);
        }

        // Gets the children of the input categoryId as well (Ex: Input 2 gives items with categoryId 3 and 4).
        if (searchProductDto.getCategoryId() != null){
            Category category = categoryRepository.findById(searchProductDto.getCategoryId()).orElse(null);
            if (category != null) {
                List<Integer> categoryIds = new ArrayList<>();
                collectCategoryIds(category, categoryIds);
                Predicate categoryPredicate = root.get("category").get("id").in(categoryIds);
                predicates.add(categoryPredicate);
            }
        }

        if (!predicates.isEmpty()) {
            criteriaQuery.where(
                    criteriaBuilder.and(predicates.toArray(new Predicate[0]))
            );
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("code")));

        TypedQuery<Product> query = entityManager.createQuery(criteriaQuery);

        // Pagination to limit return size. (Add via a Pageable object to un-hardcode.
        // query.setFirstResult(0); // offset
        // query.setMaxResults(20); // limit per fetch
        return query.getResultList();
    }

    private void collectCategoryIds(Category category, List<Integer> ids) {
        ids.add(category.getId());
        for (Category child : category.getChildren()) {
            collectCategoryIds(child, ids);
        }
    }
}

