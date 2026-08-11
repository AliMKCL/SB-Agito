package com.agito.staj.service;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.entity.Category;
import com.agito.staj.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.cache.type=simple",
        "spring.cloud.stream.bindings.registerNewProduct-out-0.destination=mock-destination",
        "spring.cloud.stream.bindings.registerDeleteProduct-out-0.destination=mock-destination",
        "spring.cloud.stream.bindings.registerEditProduct-out-0.destination=mock-destination"
})
public class CategoryCacheTest {

    private static final Logger log = LoggerFactory.getLogger(CategoryCacheTest.class);

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    private Category dummyCategory;

    @BeforeEach
    void setUp() {
        if (cacheManager.getCache("categories") != null) {
            cacheManager.getCache("categories").clear();
        }

        dummyCategory = new Category();
        dummyCategory.setId(1);
        dummyCategory.setName("Electronics");
    }

    @Test
    public void testCacheIsActuallyFaster() throws InterruptedException {
        // Stub the repository to sleep for 100ms to simulate a slow database/network call
        when(categoryRepository.findById(1)).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Optional.of(dummyCategory);
        });

        // 1st call: Cache miss. Hits the database (mocked slow response)
        long startTime = System.currentTimeMillis();
        CategoryDto firstCallResult = categoryService.findCategoryById(1);
        long firstCallDuration = System.currentTimeMillis() - startTime;

        log.info("Category 1st call duration (Cache Miss): {} ms", firstCallDuration);
        assertNotNull(firstCallResult);
        assertEquals("Electronics", firstCallResult.getName());
        assertTrue(firstCallDuration >= 100, "First call should take at least 100ms because database mock sleeps for 100ms");

        // 2nd call: Cache hit. Fetched directly from the in-memory cache, bypassing the repository mock.
        startTime = System.currentTimeMillis();
        CategoryDto secondCallResult = categoryService.findCategoryById(1);
        long secondCallDuration = System.currentTimeMillis() - startTime;

        log.info("Category 2nd call duration (Cache Hit): {} ms, object: {}", secondCallDuration, secondCallResult);
        assertNotNull(secondCallResult);
        assertEquals("Electronics", secondCallResult.getName());
        assertTrue(secondCallDuration < firstCallDuration / 2, "Second call (Cache Hit) should be significantly faster than the first call");

        // Verify that the repository's findById method was only invoked exactly once, proving the second call bypassed it
        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    public void testCacheEvictionOnDeletion() throws InterruptedException {
        // Stub repository to sleep for 100ms
        when(categoryRepository.findById(1)).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Optional.of(dummyCategory);
        });

        // Populate cache
        categoryService.findCategoryById(1);
        verify(categoryRepository, times(1)).findById(1);

        // Call findCategoryById again (cache hit)
        var startTime = System.currentTimeMillis();
        CategoryDto categoryDto = categoryService.findCategoryById(1);
        var endTime = System.currentTimeMillis();
        var diff = endTime - startTime;
        log.info("Category found in cache: retrieved in {} ms", diff);
        verify(categoryRepository, times(1)).findById(1); // count still 1

        // Trigger cache eviction (by deleting the category)
        doNothing().when(categoryRepository).delete(dummyCategory);
        categoryService.deleteCategory(1);

        // Calling findCategoryById again should miss cache and hit repository again (invocation count goes to 2)
        startTime = System.currentTimeMillis();
        try {
            categoryService.findCategoryById(1);
        } catch (Exception e) {
            // expected or handle not found since it is deleted, but we stub to sleep and return it to check cache status
        }
        endTime = System.currentTimeMillis();
        diff = endTime - startTime;
        log.info("Category not found in cache, retrieved in: {} ms", diff);
        verify(categoryRepository, times(3)).findById(1);
    }

    @Test
    public void testCacheEvictionOnEdit() throws InterruptedException {
        // Stub repository
        when(categoryRepository.findById(1)).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Optional.of(dummyCategory);
        });

        // Populate cache
        categoryService.findCategoryById(1);
        verify(categoryRepository, times(1)).findById(1);

        // Call findCategoryById again (cache hit)
        categoryService.findCategoryById(1);
        verify(categoryRepository, times(1)).findById(1); // count still 1

        // Trigger cache eviction (by editing the category)
        CategoryDto editDto = new CategoryDto();
        editDto.setId(1);
        editDto.setName("New Electronics");

        when(categoryRepository.findByName("New Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(dummyCategory);

        categoryService.editCategory(editDto);

        // Calling findCategoryById again should miss cache and hit repository again (invocation count goes to 3)
        categoryService.findCategoryById(1);
        verify(categoryRepository, times(3)).findById(1);
    }
}
