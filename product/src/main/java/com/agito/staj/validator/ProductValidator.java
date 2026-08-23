package com.agito.staj.validator;

import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.exception.*;
import com.agito.staj.util.Translator;

import java.util.Optional;

public class ProductValidator {

    public static Product validateProductExists(Optional<Product> opt, String code) {
        return opt.orElseThrow(() -> new ProductNotFoundException(Translator.toLocale("error.product.notFound", code)));
    }

    public static Category validateCategoryExists(Optional<Category> opt, int categoryId) {
        return opt.orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.notFound", categoryId)));
    }

    public static void validateCategoryIsLeaf(Category category) {
        if (!category.getChildren().isEmpty()) {
            throw new InvalidCategoryException(
                    Translator.toLocale("error.category.parentInvalid", category.getName())
            );
        }
    }

    public static void validateProductNotDuplicate(boolean exists, String code) {
        if (exists) {
            throw new DuplicateProductException(Translator.toLocale("error.product.duplicate", code));
        }
    }

    public static void validateCategoryLeafForEdit(Optional<Category> optCategory, int categoryId) {
        Category category = optCategory.orElseThrow(() -> new InvalidCategoryException(
                Translator.toLocale("error.category.newCategoryNotExist")
        ));
        if (!category.getChildren().isEmpty()) {
            throw new CategoryNotLeafException(Translator.toLocale("error.category.notLeaf"));
        }
    }
}
