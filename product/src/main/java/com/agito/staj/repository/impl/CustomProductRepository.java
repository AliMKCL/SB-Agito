package com.agito.staj.repository.impl;

import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.FilterCriteria;
import com.agito.staj.entity.Product;
import com.agito.staj.entity.SearchOperation;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.ICustomProductRepository;
import com.agito.staj.repository.IProductRepository;
import com.agito.staj.util.GenericQueryUtil;
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

import static com.agito.staj.util.GenericQueryUtil.buildPredicates;

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

        List<FilterCriteria> filters = new ArrayList<>();

        if (searchProductDto.getCode() != null){
            filters.add(new FilterCriteria("code", SearchOperation.LIKE, searchProductDto.getCode()));
        }
        if (searchProductDto.getName() != null){
            filters.add(new FilterCriteria("name", SearchOperation.LIKE, searchProductDto.getName()));
        }

        // Gets the children of the input categoryId as well (Ex: Input 2 gives items with categoryId 3 and 4).
        if (searchProductDto.getCategoryId() != null){
            Category category = categoryRepository.findById(searchProductDto.getCategoryId()).orElse(null);
            if (category != null) {
                List<Integer> categoryIds = new ArrayList<>();
                collectCategoryIds(category, categoryIds);
                filters.add(new FilterCriteria("category.id", SearchOperation.IN, categoryIds));
            }
        }

        Predicate[] predicates = buildPredicates(criteriaBuilder, root, filters);
        criteriaQuery.where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("code")));
        List<Product> results = entityManager.createQuery(criteriaQuery).getResultList();


        // Pagination to limit return size. (Add via a Pageable object to un-hardcode.
        // query.setFirstResult(0); // offset
        // query.setMaxResults(20); // limit per fetch
        return results;
    }

    private void collectCategoryIds(Category category, List<Integer> ids) {
        ids.add(category.getId());
        for (Category child : category.getChildren()) {
            collectCategoryIds(child, ids);
        }
    }
}

