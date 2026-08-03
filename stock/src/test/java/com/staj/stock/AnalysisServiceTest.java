package com.staj.stock;

import com.staj.stock.entity.Stock;
import com.staj.stock.service.AnalysisService;
import com.staj.stock.service.AnalysisService.RemainingStockReport;
import com.staj.stock.service.AnalysisService.RemainingStockItem;
import com.staj.stock.service.AnalysisService.ProfitAnalysisReport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AnalysisServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AnalysisServiceTest.class);

    @Autowired
    private AnalysisService analysisService;

    @Test
    void testRemainingStockValueReport() {
        log.info("Starting test: testRemainingStockValueReport");
        RemainingStockReport report = analysisService.getRemainingStockValueReport();

        assertNotNull(report, "Remaining stock report should not be null");
        log.info("Grand Total Cost: {}, Grand Total Sale: {}", report.getGrandTotalCostValue(), report.getGrandTotalSaleValue());

        // Assert Grand Totals
        // Product 0001 Cost: 5 * 8 = 40. Product 0002 Cost: 2 * 10 = 20. Product 0003 Cost: 3 * 5 = 15. Total Cost = 75
        assertEquals(75.00, report.getGrandTotalCostValue(), 0.01, "Grand total cost value mismatch");
        // Product 0001 Sale: 5 * 20 = 100. Product 0002 Sale: 2 * 5 = 10. Product 0003 Sale: 3 * 10 = 30. Total Sale = 140
        assertEquals(140.00, report.getGrandTotalSaleValue(), 0.01, "Grand total sale value mismatch");

        // Verify individual items
        List<RemainingStockItem> items = report.getItems();
        assertEquals(3, items.size(), "Should have exactly 3 product entries");

        // Check Product 0001
        RemainingStockItem item1 = items.stream().filter(i -> i.getCode().equals("0001")).findFirst().orElseThrow();
        assertEquals(5, item1.getQuantity());
        assertEquals(20.00, item1.getUnitSalePrice(), 0.01);
        assertEquals(8.00, item1.getAveragePurchasePrice(), 0.01);
        assertEquals(40.00, item1.getTotalCostValue(), 0.01);
        assertEquals(100.00, item1.getTotalSaleValue(), 0.01);
        log.info("Product 0001 remaining stock validated successfully.");
    }

    @Test
    void testLowStockItems() {
        log.info("Starting test: testLowStockItems");
        
        // Threshold of 4: Stock 0002 (quantity 2) and 0003 (quantity 3) should be returned
        List<Stock> lowStockItems4 = analysisService.getLowStockItems(4);
        assertEquals(2, lowStockItems4.size(), "Should find 2 items below threshold 4");
        assertTrue(lowStockItems4.stream().anyMatch(s -> s.getCode().equals("0002")));
        assertTrue(lowStockItems4.stream().anyMatch(s -> s.getCode().equals("0003")));

        // Threshold of 6: All items (quantities 5, 2, 3) should be returned
        List<Stock> lowStockItems6 = analysisService.getLowStockItems(6);
        assertEquals(3, lowStockItems6.size(), "Should find 3 items below threshold 6");
        log.info("Low stock validation checked successfully.");
    }

    @Test
    void testProductExpectedProfitReport_ProfitableProduct() {
        log.info("Starting test: testProductExpectedProfitReport_ProfitableProduct for code 0001");
        ProfitAnalysisReport report = analysisService.getProductExpectedProfitReport("0001");

        assertNotNull(report);
        assertEquals("0001", report.getCode());
        assertEquals(300.00, report.getOldSalesRevenue(), 0.01, "Old sales revenue mismatch");
        assertEquals(15, report.getOldSalesQuantity(), "Old sales quantity mismatch");
        assertEquals(5, report.getRemainingStockQuantity(), "Remaining stock quantity mismatch");
        assertEquals(100.00, report.getEstimatedRemainingSalesRevenue(), 0.01, "Remaining sales revenue estimation mismatch");
        assertEquals(400.00, report.getTotalExpectedRevenue(), 0.01, "Total expected revenue mismatch");
        assertEquals(160.00, report.getTotalPurchaseCost(), 0.01, "Total purchase cost mismatch");
        assertEquals(8.00, report.getAveragePurchasePrice(), 0.01, "Average purchase price mismatch");
        assertEquals(240.00, report.getExpectedProfit(), 0.01, "Expected profit mismatch");
        
        log.info("Profitable Product 0001 Expected Profit is: {}", report.getExpectedProfit());
    }

    @Test
    void testProductExpectedProfitReport_LossMakingProduct() {
        log.info("Starting test: testProductExpectedProfitReport_LossMakingProduct for code 0002");
        ProfitAnalysisReport report = analysisService.getProductExpectedProfitReport("0002");

        assertNotNull(report);
        assertEquals("0002", report.getCode());
        assertEquals(15.00, report.getOldSalesRevenue(), 0.01, "Old sales revenue mismatch");
        assertEquals(3, report.getOldSalesQuantity(), "Old sales quantity mismatch");
        assertEquals(2, report.getRemainingStockQuantity(), "Remaining stock quantity mismatch");
        assertEquals(10.00, report.getEstimatedRemainingSalesRevenue(), 0.01, "Remaining sales revenue estimation mismatch");
        assertEquals(25.00, report.getTotalExpectedRevenue(), 0.01, "Total expected revenue mismatch");
        assertEquals(50.00, report.getTotalPurchaseCost(), 0.01, "Total purchase cost mismatch");
        assertEquals(10.00, report.getAveragePurchasePrice(), 0.01, "Average purchase price mismatch");
        
        // Assert loss (negative profit)
        assertTrue(report.getExpectedProfit() < 0, "Expected profit should be negative (a loss)");
        assertEquals(-25.00, report.getExpectedProfit(), 0.01, "Expected loss mismatch");

        log.info("Loss-making Product 0002 Expected Loss is: {}", report.getExpectedProfit());
    }
}
