package com.agito.staj.i18n;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.util.Translator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.stream.bindings.registerNewProduct-out-0.destination=mock-dest",
        "spring.cloud.stream.bindings.registerDeleteProduct-out-0.destination=mock-dest",
        "spring.cloud.stream.bindings.registerEditProduct-out-0.destination=mock-dest"
})
public class I18nIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testTranslatorDirect_EnglishAndTurkish() {
        // English (default)
        String msgEn = Translator.toLocale("error.product.notFound", Locale.ENGLISH, "P100");
        assertEquals("No product with code: P100", msgEn);

        // Turkish
        String msgTr = Translator.toLocale("error.product.notFound", Locale.forLanguageTag("tr"), "P100");
        assertEquals("P100 kodlu ürün bulunamadı.", msgTr);

        // Fallback for unsupported locale (e.g. French -> English fallback)
        String msgFr = Translator.toLocale("error.product.notFound", Locale.FRENCH, "P100");
        assertEquals("No product with code: P100", msgFr);
    }

    @Test
    void testCategoryNotFoundException_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/Category/fetch")
                        .param("id", "99999")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("99999 ID numaralı kategori bulunamadı."));
    }

    @Test
    void testCategoryNotFoundException_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/Category/fetch")
                        .param("id", "99999")
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Category not found with ID: 99999"));
    }

    @Test
    void testProductNotFoundException_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/fetch")
                        .param("code", "Z999")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Z999 kodlu ürün bulunamadı."));
    }

    @Test
    void testProductNotFoundException_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/fetch")
                        .param("code", "Z999")
                        .header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("No product with code: Z999"));
    }

    @Test
    void testValidationErrors_LocalizedInTurkish() throws Exception {
        CategoryDto invalidCategory = new CategoryDto();
        invalidCategory.setName(""); // Empty name triggers @NotEmpty

        mockMvc.perform(post("/apiAdmin/Category/create")
                        .header("Accept-Language", "tr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void testValidationErrors_LocalizedInEnglish() throws Exception {
        CategoryDto invalidCategory = new CategoryDto();
        invalidCategory.setName(""); // Empty name triggers @NotEmpty

        mockMvc.perform(post("/apiAdmin/Category/create")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void testProductValidationErrors_LocalizedInTurkish() throws Exception {
        ProductDto invalidProduct = new ProductDto();
        invalidProduct.setCode("1"); // Length != 4
        invalidProduct.setName("");
        invalidProduct.setPrice(-5.0); // Price < 0

        mockMvc.perform(post("/apiAdmin/create")
                        .header("Accept-Language", "tr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Ürün kodunun uzunluğu tam olarak 4 karakter olmalıdır."))
                .andExpect(jsonPath("$.price").value("Fiyat 0 veya daha büyük olmalıdır."));
    }

    @Test
    void testProductValidationErrors_LocalizedInEnglish() throws Exception {
        ProductDto invalidProduct = new ProductDto();
        invalidProduct.setCode("1"); // Length != 4
        invalidProduct.setName("");
        invalidProduct.setPrice(-5.0); // Price < 0

        mockMvc.perform(post("/apiAdmin/create")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Length of the product code must be exactly 4."))
                .andExpect(jsonPath("$.price").value("Price must be greater than or equal to 0."));
    }

    @Test
    void testRequestParamValidation_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/fetch")
                        .param("code", "12") // Size != 4
                        .header("Accept-Language", "tr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Ürün kodu uzunluğu tam olarak 4 olmalıdır"));
    }

    @Test
    void testRequestParamValidation_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/fetch")
                        .param("code", "12") // Size != 4
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Code length must be exactly 4"));
    }
}
