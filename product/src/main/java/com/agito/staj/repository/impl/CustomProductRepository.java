package com.agito.staj.repository.impl;

import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.entity.ProductTranslation;
import com.agito.staj.exception.IncompatibleTypesException;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.ICustomProductRepository;
import com.agito.staj.util.Translator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class CustomProductRepository implements ICustomProductRepository {

    private final EntityManager entityManager;
    private final CategoryRepository categoryRepository;

    public CustomProductRepository(EntityManager entityManager, CategoryRepository categoryRepository) {
        this.entityManager = entityManager;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> findAllByCriteria(SearchProductDto searchProductDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        Root<Product> root = criteriaQuery.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        if (searchProductDto.getCode() != null && !searchProductDto.getCode().isBlank()) {
            String pattern = "%" + searchProductDto.getCode().toLowerCase() + "%";
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), pattern));
        }

        if (searchProductDto.getName() != null && !searchProductDto.getName().isBlank()) {
            Join<Product, ProductTranslation> translationJoin = root.join("translations", JoinType.LEFT);
            Locale currentLocale = LocaleContextHolder.getLocale();
            String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
            String pattern = "%" + searchProductDto.getName().toLowerCase() + "%";

            Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("name")), pattern);
            Predicate langMatch = criteriaBuilder.equal(translationJoin.get("languageCode"), activeLang);
            predicates.add(criteriaBuilder.and(langMatch, nameMatch));
        }

        // Gets the children of the input categoryId as well (Ex: Input 2 gives items with categoryId 3 and 4).
        Category category = searchProductDto.getCategoryId() != null
                ? categoryRepository.findById(searchProductDto.getCategoryId()).orElse(null)
                : null;
        if (category != null) {
            List<Integer> categoryIds = new ArrayList<>();
            collectCategoryIds(category, categoryIds);
            predicates.add(root.get("category").get("id").in(categoryIds));
        }

        // Parses the price filter string (Ex: ">100.0" or "<500") into a GREATER_THAN or LESS_THAN predicate.
        if (searchProductDto.getPrice() != null && !searchProductDto.getPrice().isBlank()) {
            String priceFilter = searchProductDto.getPrice().trim();
            char operator = priceFilter.charAt(0);
            String numericPart = priceFilter.substring(1);

            double parsedPrice;
            try {
                parsedPrice = Double.parseDouble(numericPart);
            } catch (NumberFormatException e) {
                throw new IncompatibleTypesException(
                        Translator.toLocale("error.search.priceIncompatible", numericPart)
                );
            }

            if (operator == '>') {
                predicates.add(criteriaBuilder.greaterThan(root.get("price"), parsedPrice));
            } else if (operator == '<') {
                predicates.add(criteriaBuilder.lessThan(root.get("price"), parsedPrice));
            }
        }

        criteriaQuery.select(root).distinct(true);
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("code")));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Used to find all items belonging to a child category of a category used during filtering.
     *
     * @param category
     * @param ids
     */
    private void collectCategoryIds(Category category, List<Integer> ids) {
        ids.add(category.getId());
        for (Category child : category.getChildren()) {
            collectCategoryIds(child, ids);
        }
    }
}
