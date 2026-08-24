package com.staj.stock.schedulers;

import com.staj.stock.entity.Stock;
import com.staj.stock.service.AnalysisService;
import com.staj.stock.service.MailService;
import com.staj.stock.util.Translator;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
public class StockCheckScheduler {

    private static final Logger log = LoggerFactory.getLogger(StockCheckScheduler.class);

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private MailService mailService;

    @Value("${MAIL_ADDR:test@example.com}")
    private String mailAddr;

    @Value("${MAIL_LOCALE:en}")
    private String mailLocaleTag;

    public StockCheckScheduler(AnalysisService analysisService, MailService mailService) {
        this.analysisService = analysisService;
        this.mailService = mailService;
    }

    /**
     * Method called by the scheduler every day to send a mail regarding low stock.
     */
    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Istanbul")
    public void checkStockBelowThreshold() throws MessagingException, IOException {
        Locale locale = LocaleContextHolder.getLocale();
        checkStockBelowThreshold(locale);
    }

    /**
     * Overloaded method allowing explicit locale specification (ex: for manual execution or localized alerts).
     *
     * @param locale Target locale for the email content
     */
    public void checkStockBelowThreshold(Locale locale) throws MessagingException, IOException {
        Locale effectiveLocale = locale != null ? locale : Locale.ENGLISH;
        List<Stock> lowStockItems = analysisService.getLowStockItems();
        String subject = Translator.toLocale("mail.lowStock.subject", effectiveLocale);

        StringBuilder bodyBuilder = new StringBuilder();
        if (lowStockItems.isEmpty()) {
            bodyBuilder.append(Translator.toLocale("mail.lowStock.noItems", effectiveLocale));
        } else {
            bodyBuilder.append(Translator.toLocale("mail.lowStock.body", effectiveLocale)).append("\n\n");
            for (Stock item : lowStockItems) {
                bodyBuilder.append(Translator.toLocale("mail.lowStock.item", effectiveLocale,
                        item.getCode(), item.getQuantity(), item.getThreshold())).append("\n");
            }
        }

        log.info("[SCHEDULER] Sending low stock alert email to {} in locale {}", mailAddr, effectiveLocale.toLanguageTag());
        mailService.sendEmail(mailAddr, subject, bodyBuilder.toString());
    }
}
