package com.agito.staj.service;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.repository.IProductRepository;
import com.agito.staj.repository.impl.CustomProductRepository;
import com.agito.staj.service.client.StockFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private CustomProductRepository customProductRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StockFeignClient stockFeignClient;

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private ProductService productService;

    private Category laptopCategory;
    private Category desktopCategory;

    @BeforeEach
    void setUp() {
        laptopCategory = new Category();
        laptopCategory.setId(3);
        laptopCategory.setName("Laptops");

        desktopCategory = new Category();
        desktopCategory.setId(4);
        desktopCategory.setName("Desktops");
    }

    @Test
    void testSeedDatabase_Success() {
        // Arrange
        when(categoryRepository.findById(3)).thenReturn(Optional.of(laptopCategory));
        when(categoryRepository.findById(4)).thenReturn(Optional.of(desktopCategory));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(streamBridge.send(anyString(), any())).thenReturn(true);

        // Act
        List<ProductDto> seeded = productService.seedDatabase();

        // Assert
        assertNotNull(seeded);
        assertEquals(30, seeded.size());
        verify(productRepository, times(30)).save(any(Product.class));
        verify(streamBridge, times(30)).send(eq("registerNewProduct-out-0"), any());
    }

    @Test
    void testSeedDatabase_AlreadySeeded() {
        // Arrange
        when(productRepository.findByCode(anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            Product dummy = new Product();
            dummy.setCode(code);
            return Optional.of(dummy);
        });

        // Act
        List<ProductDto> seeded = productService.seedDatabase();

        // Assert
        assertNotNull(seeded);
        assertTrue(seeded.isEmpty());
        verify(productRepository, never()).save(any(Product.class));
        verify(streamBridge, never()).send(anyString(), any());
    }
}
