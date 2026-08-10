package com.agito.staj.service;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.cache.type=simple",
        "spring.cloud.stream.bindings.registerNewProduct-out-0.destination=mock-destination",
        "spring.cloud.stream.bindings.registerDeleteProduct-out-0.destination=mock-destination",
        "spring.cloud.stream.bindings.registerEditProduct-out-0.destination=mock-destination"
})
public class ProductCacheTest {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheTest.class);

    @Autowired
    private ProductService productService;

    @MockitoBean
    private IProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    private Product dummyProduct;

    @BeforeEach
    void setUp() {
        // Clear caches before each test
        if (cacheManager.getCache("products") != null) {
            cacheManager.getCache("products").clear();
        }

        Category category = new Category();
        category.setId(1);
        category.setName("Electronics");

        dummyProduct = new Product("0001", "Laptop", category, 1200.0, true);
        productRepository.save(dummyProduct);
    }

    @Test
    public void testCacheIsActuallyFaster() throws InterruptedException {
        // Stub the repository to sleep for 100ms to simulate a slow database/network call
        when(productRepository.findByCode("0001")).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Optional.of(dummyProduct);
        });

        // 1st call: Cache miss. Hits the database (mocked slow response)
        long startTime = System.currentTimeMillis();
        ProductDto firstCallResult = productService.find("0001");
        long firstCallDuration = System.currentTimeMillis() - startTime;

        log.info("First call duration (Cache Miss): {} ms", firstCallDuration);
        assertNotNull(firstCallResult);
        assertEquals("0001", firstCallResult.getCode());
        assertTrue(firstCallDuration >= 100, "First call should take at least 100ms because database mock sleeps for 100ms");

        // 2nd call: Cache hit. Fetched directly from the in-memory cache, bypassing the repository mock.
        startTime = System.currentTimeMillis();
        ProductDto secondCallResult = productService.find("0001");
        long secondCallDuration = System.currentTimeMillis() - startTime;

        log.info("Second call duration (Cache Hit): {} ms, object: {}", secondCallDuration, secondCallResult);
        assertNotNull(secondCallResult);
        assertEquals("0001", secondCallResult.getCode());
        assertTrue(secondCallDuration < firstCallDuration / 2, "Second call (Cache Hit) should be significantly faster than the first call");

        // Verify that the repository's findByCode method was only invoked exactly once, proving the second call bypassed it
        verify(productRepository, times(1)).findByCode("0001");
    }

    @Test
    public void testCacheEvictionOnDeletion() throws InterruptedException {
        // Stub repository to sleep for 50ms
        when(productRepository.findByCode("0001")).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Optional.of(dummyProduct);
        });

        // Populates cache
        productService.find("0001");
        verify(productRepository, times(1)).findByCode("0001");

        // Call find again (cache hit)
        var startTime = System.currentTimeMillis();
        ProductDto productDto = productService.find("0001");
        var endTime = System.currentTimeMillis();
        var diff = endTime - startTime;
        log.info("Product found in cache: retreived in {} ms", diff);
        verify(productRepository, times(1)).findByCode("0001"); // count still 1

        // Trigger cache eviction (by deleting the product local)
        productService.deleteProductLocal("0001");

        // Calling find again should miss cache and hit repository again (invocation count goes to 3)
        startTime = System.currentTimeMillis();
        productService.find("0001");
        endTime = System.currentTimeMillis();
        diff = endTime - startTime;
        log.info("Product not found in cache, retreived in: {} ms", diff);
        verify(productRepository, times(3)).findByCode("0001");
    }
}
