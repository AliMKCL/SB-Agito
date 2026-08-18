package com.agito.staj.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FilterCriteria {
    private String fieldName; // code, name, categoryId
    private SearchOperation operation;
    private Object value;
}
