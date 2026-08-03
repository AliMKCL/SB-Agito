package com.staj.stock.schedulers;

import com.staj.stock.entity.Stock;
import com.staj.stock.repository.StockRepository;
import com.staj.stock.service.AnalysisService;
import com.staj.stock.service.MailService;
import com.staj.stock.service.StockService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class StockCheckScheduler {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private MailService mailService;

    public StockCheckScheduler(AnalysisService analysisService, MailService mailService) {
        this.analysisService = analysisService;
        this.mailService = mailService;
    }

    @Value("${MAIL_ADDR:test@example.com}")
    private String mailAddr;


    /**
     * Method called by the scheduler every day to send a mail regarding low stock.
     */
    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Istanbul")
    public void checkStockBelowThreshold() throws MessagingException, IOException {

        // Threshold defined by 10, form here for now.
        List<Stock> lowStockItems = analysisService.getLowStockItems(10);
        mailService.sendEmail(
                mailAddr,
                "Low stock items",
                "Items low on stock: " + lowStockItems.toString());


    }
}
