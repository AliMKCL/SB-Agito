package com.agito.staj.validator;

import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.exception.*;

import java.util.Optional;

public class ProductValidator {

    public static Product validateProductExists(Optional<Product> opt, String code) {
        return opt.orElseThrow(() -> new ProductNotFoundException("No product with code: " + code));
    }

    public static Category validateCategoryExists(Optional<Category> opt, int categoryId) {
        return opt.orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));
    }

    public static void validateCategoryIsLeaf(Category category) {
        if (!category.getChildren().isEmpty()) {
            throw new InvalidCategoryException(
                    "Products can only be assigned to leaf categories (end of inheritance line). " +
                            "Category '" + category.getName() + "' is a parent category."
            );
        }
    }

    public static void validateProductNotDuplicate(boolean exists, String code) {
        if (exists) {
            throw new DuplicateProductException("There is already a product with code " + code);
        }
    }

    public static void validateCategoryLeafForEdit(Optional<Category> optCategory, int categoryId) {
        Category category = optCategory.orElseThrow(() -> new InvalidCategoryException("The new category of the item does not exist"));
        if (!category.getChildren().isEmpty()) {
            throw new CategoryNotLeafException("The new category of the item is not a leaf category");
        }
    }
}
