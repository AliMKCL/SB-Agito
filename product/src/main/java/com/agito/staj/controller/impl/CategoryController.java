package com.agito.staj.controller.impl;

import com.agito.staj.controller.ICategoryController;
import com.agito.staj.dto.CategoryDto;
import com.agito.staj.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CategoryController implements ICategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public ResponseEntity<CategoryDto> createCategory(CategoryDto categoryDto) {
        CategoryDto created = categoryService.createCategory(categoryDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Override
    public ResponseEntity<CategoryDto> findCategory(Integer id) {
        CategoryDto categoryDto = categoryService.findCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDto);
    }

    @Override
    public ResponseEntity<List<CategoryDto>> findAllCategories() {
        List<CategoryDto> categories = categoryService.findAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @Override
    public ResponseEntity<CategoryDto> editCategory(CategoryDto categoryDto) {
        CategoryDto edited = categoryService.editCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
