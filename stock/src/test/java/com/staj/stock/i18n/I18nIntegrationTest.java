package com.staj.stock.i18n;

import com.staj.stock.schedulers.StockCheckScheduler;
import com.staj.stock.service.MailService;
import com.staj.stock.util.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.stream.bindings.createNewProduct-in-0.destination=mock-dest",
        "spring.cloud.stream.bindings.confirmNewProduct-out-0.destination=mock-dest",
        "spring.cloud.stream.bindings.deleteProduct-in-0.destination=mock-dest",
        "spring.cloud.stream.bindings.confirmDeleteProduct-out-0.destination=mock-dest",
        "spring.cloud.stream.bindings.editProduct-in-0.destination=mock-dest",
        "spring.cloud.stream.bindings.confirmEditProduct-out-0.destination=mock-dest"
})
@ActiveProfiles("test")
public class I18nIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StockCheckScheduler stockCheckScheduler;

    @MockitoSpyBean
    private MailService mailService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testTranslatorDirect_EnglishAndTurkish() {
        // English (default)
        String msgEn = Translator.toLocale("error.stock.itemNotFound", Locale.ENGLISH, "P100");
        assertEquals("Item not found in stock database with code: P100", msgEn);

        // Turkish
        String msgTr = Translator.toLocale("error.stock.itemNotFound", Locale.forLanguageTag("tr"), "P100");
        assertEquals("P100 kodlu ürün stok veritabanında bulunamadı.", msgTr);

        // Fallback for unsupported locale (e.g. French -> English fallback)
        String msgFr = Translator.toLocale("error.stock.itemNotFound", Locale.FRENCH, "P100");
        assertEquals("Item not found in stock database with code: P100", msgFr);
    }

    @Test
    void testItemNotFoundException_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/checkStock")
                        .param("code", "Z999")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Z999 kodlu ürün stok veritabanında bulunamadı."));
    }

    @Test
    void testItemNotFoundException_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/checkStock")
                        .param("code", "Z999")
                        .header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Item not found in stock database with code: Z999"));
    }

    @Test
    void testStockOutOfBoundsException_LocalizedInTurkish() throws Exception {
        // First add stock
        mockMvc.perform(post("/apiAdmin/addStockAuto")
                        .param("code", "T100")
                        .param("quantity", "5")
                        .param("unitPrice", "10.0"))
                .andExpect(status().isOk());

        // Try to remove more than available stock
        mockMvc.perform(post("/apiConsumer/removeStock")
                        .param("code", "T100")
                        .param("quantity", "999")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Yetersiz stok miktarı."));
    }

    @Test
    void testStockOutOfBoundsException_LocalizedInEnglish() throws Exception {
        // First add stock
        mockMvc.perform(post("/apiAdmin/addStockAuto")
                        .param("code", "T200")
                        .param("quantity", "5")
                        .param("unitPrice", "10.0"))
                .andExpect(status().isOk());

        // Try to remove more than available stock
        mockMvc.perform(post("/apiConsumer/removeStock")
                        .param("code", "T200")
                        .param("quantity", "999")
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not enough stock available"));
    }

    @Test
    void testNegativeThreshold_LocalizedInTurkish() throws Exception {
        mockMvc.perform(put("/apiAdmin/editThreshold")
                        .param("code", "T100")
                        .param("threshold", "-1")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.threshold").value("Miktar 0 veya daha büyük olmalıdır"));
    }

    @Test
    void testNegativeThreshold_LocalizedInEnglish() throws Exception {
        mockMvc.perform(put("/apiAdmin/editThreshold")
                        .param("code", "T100")
                        .param("threshold", "-1")
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.threshold").value("Quantity must be greater than or equal to 0"));
    }

    @Test
    void testRequestParamValidation_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/checkStock")
                        .param("code", "1") // Size != 4
                        .header("Accept-Language", "tr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Ürün kodu uzunluğu tam olarak 4 olmalıdır"));
    }

    @Test
    void testRequestParamValidation_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/checkStock")
                        .param("code", "1") // Size != 4
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Code length must be exactly 4"));
    }

    @Test
    void testMailController_SendEmailSuccess_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/send_email")
                        .param("to", "test@example.com")
                        .param("subject", "Test Subject")
                        .param("body", "Test Body")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isOk())
                .andExpect(content().string("E-posta başarıyla gönderildi!"));
    }

    @Test
    void testMailController_SendEmailSuccess_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/send_email")
                        .param("to", "test@example.com")
                        .param("subject", "Test Subject")
                        .param("body", "Test Body")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));
    }

    @Test
    void testMailController_Validation_LocalizedInTurkish() throws Exception {
        mockMvc.perform(get("/apiAdmin/send_email")
                        .param("to", "not-an-email")
                        .param("subject", "")
                        .param("body", "Test Body")
                        .header("Accept-Language", "tr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.to").value("Geçersiz e-posta adresi formatı"))
                .andExpect(jsonPath("$.subject").value("E-posta konusu boş bırakılamaz"));
    }

    @Test
    void testMailController_Validation_LocalizedInEnglish() throws Exception {
        mockMvc.perform(get("/apiAdmin/send_email")
                        .param("to", "not-an-email")
                        .param("subject", "")
                        .param("body", "Test Body")
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.to").value("Invalid email address format"))
                .andExpect(jsonPath("$.subject").value("Email subject cannot be blank"));
    }

    @Test
    void testStockCheckScheduler_LocalizedInTurkish() throws Exception {
        stockCheckScheduler.checkStockBelowThreshold(Locale.forLanguageTag("tr"));

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(mailService).sendEmail(anyString(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("Kritik Stok Uyarısı", subjectCaptor.getValue());
    }

    @Test
    void testStockCheckScheduler_LocalizedInEnglish() throws Exception {
        stockCheckScheduler.checkStockBelowThreshold(Locale.ENGLISH);

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(mailService).sendEmail(anyString(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("Low Stock Alert", subjectCaptor.getValue());
    }
}
