package com.agito.staj.service;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.entity.Category;
import com.agito.staj.exception.CategoryNotFoundException;
import com.agito.staj.exception.DuplicateCategoryException;
import com.agito.staj.exception.InvalidCategoryException;
import com.agito.staj.util.Translator;
import com.agito.staj.mapper.CategoryMapper;
import com.agito.staj.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new DuplicateCategoryException(Translator.toLocale("error.category.duplicate", categoryDto.getName()));
        }

        Category parent = null;
        if (categoryDto.getParentId() != null) {
            parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.parentNotFound", categoryDto.getParentId())));
        }

        Category category = CategoryMapper.CategoryDtoToEntity(categoryDto, parent);
        category = categoryRepository.save(category);
        return CategoryMapper.CategoryEntityToDto(category);
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDto findCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.notFound", id)));
        return CategoryMapper.CategoryEntityToDto(category);
    }

    public List<CategoryDto> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryMapper.ListCategoryEntityToDto(categories);
    }

    @CacheEvict(value = "categories", key = "#categoryDto.getId()")
    public CategoryDto editCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.notFound", categoryDto.getId())));

        // Check duplicate name
        Optional<Category> existingByName = categoryRepository.findByName(categoryDto.getName());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(category.getId())) {
            throw new DuplicateCategoryException(Translator.toLocale("error.category.duplicate", categoryDto.getName()));
        }

        // Circular parent check
        if (categoryDto.getParentId() != null) {
            if (categoryDto.getParentId().equals(category.getId())) {
                throw new InvalidCategoryException(Translator.toLocale("error.category.selfParent"));
            }
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.parentNotFound", categoryDto.getParentId())));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category.setName(categoryDto.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.CategoryEntityToDto(category);
    }

    @CacheEvict(value = "categories", key = "#id")
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.notFound", id)));
        categoryRepository.delete(category);
    }
}
