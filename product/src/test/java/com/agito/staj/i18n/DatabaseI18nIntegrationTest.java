package com.agito.staj.i18n;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.repository.CategoryRepository;
import com.agito.staj.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cache.type=simple",
        "spring.cloud.stream.bindings.registerNewProduct-out-0.destination=mock-dest",
        "spring.cloud.stream.bindings.registerDeleteProduct-out-0.destination=mock-dest",
        "spring.cloud.stream.bindings.registerEditProduct-out-0.destination=mock-dest"
})
@Transactional
public class DatabaseI18nIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        productService.seedDatabase();
    }

    @Test
    void testFetchCategory_EnglishAndTurkish() throws Exception {
        // English
        mockMvc.perform(get("/apiAdmin/Category/fetch")
                        .param("id", "3")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Laptops"));

        // Turkish
        mockMvc.perform(get("/apiAdmin/Category/fetch")
                        .param("id", "3")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Dizüstü Bilgisayarlar"));
    }

    @Test
    void testCategoryPath_HierarchyLocalization() {
        Category leafCategory = categoryRepository.findById(3).orElse(null);
        assertNotNull(leafCategory);

        // English path
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String englishPath = leafCategory.getCategoryPath();
        assertEquals("Electronics --> Computers --> Laptops", englishPath);

        // Turkish path
        LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
        String turkishPath = leafCategory.getCategoryPath();
        assertEquals("Elektronik --> Bilgisayarlar --> Dizüstü Bilgisayarlar", turkishPath);
    }

    @Test
    void testFetchProduct_EnglishAndTurkish() throws Exception {
        // English
        mockMvc.perform(get("/apiConsumer/fetch")
                        .param("code", "D001")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("D001"))
                .andExpect(jsonPath("$.name").value("iMac 24"));

        // Turkish
        mockMvc.perform(get("/apiConsumer/fetch")
                        .param("code", "D001")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("D001"))
                .andExpect(jsonPath("$.name").value("iMac 24 Masaüstü"));
    }

    @Test
    void testFallbackLocale_WhenUnsupportedRequested() throws Exception {
        // French requested -> falls back to English
        mockMvc.perform(get("/apiConsumer/fetch")
                        .param("code", "D001")
                        .header("Accept-Language", "fr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("D001"))
                .andExpect(jsonPath("$.name").value("iMac 24"));
    }

    @Test
    void testSearchProduct_LocalizedCriteriaSearch() throws Exception {
        SearchProductDto searchTr = new SearchProductDto();
        searchTr.setName("Masaüstü");

        // Search for "Masaüstü" with Turkish locale -> matches D001
        mockMvc.perform(post("/apiConsumer/fetchAll")
                        .header("Accept-Language", "tr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchTr)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code == 'D001')]").exists());

        SearchProductDto searchEn = new SearchProductDto();
        searchEn.setName("Zephyrus");

        // Search for "Zephyrus" with English locale -> matches L006
        mockMvc.perform(post("/apiConsumer/fetchAll")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchEn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code == 'L006')]").exists());
    }

    @Test
    void testCreateProduct_InActiveLocale() throws Exception {
        ProductDto newProduct = new ProductDto();
        newProduct.setCode("P999");
        newProduct.setName("Akıllı Telefon");
        newProduct.setCategoryId(3);
        newProduct.setPrice(799.0);

        // Create with Turkish locale
        mockMvc.perform(post("/apiAdmin/create")
                        .header("Accept-Language", "tr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("P999"))
                .andExpect(jsonPath("$.name").value("Akıllı Telefon"));

        // Fetch in Turkish
        mockMvc.perform(get("/apiConsumer/fetch")
                        .param("code", "P999")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Akıllı Telefon"));

        // Fetch in English (fallback since en translation was not explicitly provided)
        mockMvc.perform(get("/apiConsumer/fetch")
                        .param("code", "P999")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Akıllı Telefon"));
    }
}
