package com.agito.staj.service;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.entity.Category;
import com.agito.staj.exception.CategoryNotFoundException;
import com.agito.staj.exception.DuplicateCategoryException;
import com.agito.staj.exception.InvalidCategoryException;
import com.agito.staj.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category childCategory;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        parentCategory = new Category();
        parentCategory.setId(1);
        parentCategory.setName("Electronics");

        childCategory = new Category();
        childCategory.setId(2);
        childCategory.setName("Laptops");
        childCategory.setParent(parentCategory);

        categoryDto = new CategoryDto();
        categoryDto.setName("Laptops");
        categoryDto.setParentId(1);
    }

    @Test
    void testCreateCategory_Success() {
        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(childCategory);

        CategoryDto result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("Laptops", result.getName());
        assertEquals(1, result.getParentId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_DuplicateName() {
        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.of(childCategory));

        assertThrows(DuplicateCategoryException.class, () -> categoryService.createCategory(categoryDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testCreateCategory_ParentNotFound() {
        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.createCategory(categoryDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testFindCategoryById_Success() {
        when(categoryRepository.findById(2)).thenReturn(Optional.of(childCategory));

        CategoryDto result = categoryService.findCategoryById(2);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Laptops", result.getName());
    }

    @Test
    void testFindCategoryById_NotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findCategoryById(99));
    }

    @Test
    void testFindAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(parentCategory, childCategory));

        List<CategoryDto> results = categoryService.findAllCategories();

        assertEquals(2, results.size());
        assertEquals("Electronics", results.get(0).getName());
        assertEquals("Laptops", results.get(1).getName());
    }

    @Test
    void testEditCategory_Success() {
        CategoryDto editDto = new CategoryDto();
        editDto.setId(2);
        editDto.setName("Gaming Laptops");
        editDto.setParentId(1);

        when(categoryRepository.findById(2)).thenReturn(Optional.of(childCategory));
        when(categoryRepository.findByName("Gaming Laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDto result = categoryService.editCategory(editDto);

        assertNotNull(result);
        assertEquals("Gaming Laptops", result.getName());
        assertEquals(1, result.getParentId());
    }

    @Test
    void testEditCategory_SelfParent() {
        CategoryDto editDto = new CategoryDto();
        editDto.setId(2);
        editDto.setName("Laptops");
        editDto.setParentId(2);

        when(categoryRepository.findById(2)).thenReturn(Optional.of(childCategory));
        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.of(childCategory));

        assertThrows(InvalidCategoryException.class, () -> categoryService.editCategory(editDto));
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.findById(2)).thenReturn(Optional.of(childCategory));
        doNothing().when(categoryRepository).delete(childCategory);

        assertDoesNotThrow(() -> categoryService.deleteCategory(2));
        verify(categoryRepository, times(1)).delete(childCategory);
    }
}
