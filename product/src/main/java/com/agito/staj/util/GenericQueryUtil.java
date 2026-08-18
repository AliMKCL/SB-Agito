package com.agito.staj.util;

import com.agito.staj.entity.FilterCriteria;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GenericQueryUtil {

    public static <T> Predicate[] buildPredicates(
            CriteriaBuilder cb,
            Root<T> root,
            List<FilterCriteria> filters) {

        List<Predicate> predicates = new ArrayList<>();

        for (FilterCriteria filter : filters) {
            if (filter.getValue() == null) {
                continue;
            }

            Path<Object> path = getPath(root, filter.getFieldName());

            switch (filter.getOperation()) {
                case LIKE -> {
                    String pattern = "%" + filter.getValue().toString().toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(path.as(String.class)), pattern));
                }
                case EQUAL -> predicates.add(cb.equal(path, filter.getValue()));
                case IN -> {
                    if (filter.getValue() instanceof Collection<?> col && !col.isEmpty()) {
                        predicates.add(path.in(col));
                    }
                }
                case GREATER_THAN -> {
                    if (filter.getValue() instanceof Comparable comp) {
                        predicates.add(cb.greaterThan(path.as(Comparable.class), comp));
                    }
                }
                case LESS_THAN -> {
                    if (filter.getValue() instanceof Comparable comp) {
                        predicates.add(cb.lessThan(path.as(Comparable.class), comp));
                    }
                }
            }
        }

        return predicates.toArray(new Predicate[0]);
    }

    // Supports nested path traversal like "category.id"
    private static <T> Path<Object> getPath(Root<T> root, String fieldName) {
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.");
            Path<Object> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
        return root.get(fieldName);
    }
}