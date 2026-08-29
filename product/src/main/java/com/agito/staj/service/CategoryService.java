package com.agito.staj.service;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.entity.Category;
import com.agito.staj.exception.CategoryNotFoundException;
import com.agito.staj.exception.DuplicateCategoryException;
import com.agito.staj.exception.InvalidCategoryException;
import com.agito.staj.mapper.CategoryMapper;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.util.Translator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @CacheEvict(value = "categories", allEntries = true)
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
        Locale currentLocale = LocaleContextHolder.getLocale();
        String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
        category.addOrUpdateTranslation(activeLang, categoryDto.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.CategoryEntityToDto(category);
    }

    @Cacheable(value = "categories", key = "#id + '_' + (T(org.springframework.context.i18n.LocaleContextHolder).getLocale() != null ? T(org.springframework.context.i18n.LocaleContextHolder).getLocale().getLanguage() : 'en')")
    public CategoryDto findCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.notFound", id)));
        return CategoryMapper.CategoryEntityToDto(category);
    }

    public List<CategoryDto> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryMapper.ListCategoryEntityToDto(categories);
    }

    @CacheEvict(value = "categories", allEntries = true)
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

        Locale currentLocale = LocaleContextHolder.getLocale();
        String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
        category.addOrUpdateTranslation(activeLang, categoryDto.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.CategoryEntityToDto(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(Translator.toLocale("error.category.notFound", id)));
        categoryRepository.delete(category);
    }
}
