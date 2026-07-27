package com.staj.stock.service;

import com.staj.stock.entity.Stock;
import com.staj.stock.entity.StockEntry;
import com.staj.stock.entity.StockSale;
import com.staj.stock.exception.ItemNotFoundException;
import com.staj.stock.validator.StockValidator;
import com.staj.stock.repository.StockEntryRepository;
import com.staj.stock.repository.StockRepository;
import com.staj.stock.repository.StockSaleRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AnalysisService {

    private final StockRepository stockRepository;
    private final StockEntryRepository stockEntryRepository;
    private final StockSaleRepository stockSaleRepository;

    /**
     * Calculates the price of remaining stock (per code and grand total).
     * Provides both the Cost Value (purchase cost) and Sale Value (expected sale price).
     */
    public RemainingStockReport getRemainingStockValueReport() {
        List<Stock> stocks = stockRepository.findAll();
        List<RemainingStockItem> items = new ArrayList<>();
        double grandTotalCostValue = 0.0;
        double grandTotalSaleValue = 0.0;

        for (Stock stock : stocks) {
            List<StockEntry> entries = stockEntryRepository.findByCode(stock.getCode());
            double totalPaid = entries.stream().mapToDouble(StockEntry::getTotalPricePaid).sum();
            int totalAdded = entries.stream().mapToInt(StockEntry::getQuantityAdded).sum();
            double avgPurchasePrice = totalAdded > 0 ? (totalPaid / totalAdded) : 0.0;

            double totalCostValue = stock.getQuantity() * avgPurchasePrice;
            double totalSaleValue = stock.getQuantity() * stock.getUnitSalePrice();

            RemainingStockItem item = new RemainingStockItem(
                    stock.getCode(),
                    stock.getQuantity(),
                    stock.getUnitSalePrice(),
                    avgPurchasePrice,
                    totalCostValue,
                    totalSaleValue
            );
            items.add(item);

            grandTotalCostValue += totalCostValue;
            grandTotalSaleValue += totalSaleValue;
        }

        return new RemainingStockReport(items, grandTotalCostValue, grandTotalSaleValue);
    }

    /**
     * Retrieves items that are low on stock (below a threshold).
     */
    public List<Stock> getLowStockItems(int threshold) {
        return stockRepository.findAll().stream()
                .filter(stock -> stock.getQuantity() < threshold)
                .collect(Collectors.toList());
    }

    /**
     * Calculates expected profit if stock is finished for a product.
     * Based on all entries in stockEntry and stockSale tables for that product.
     */
    public ProfitAnalysisReport getProductExpectedProfitReport(String code) {
        Stock stock = StockValidator.validateStockExists(stockRepository.findById(code), code);

        List<StockSale> sales = stockSaleRepository.findByCode(code);
        List<StockEntry> entries = stockEntryRepository.findByCode(code);

        double oldSalesRevenue = sales.stream().mapToDouble(StockSale::getPayment).sum();
        int oldSalesQuantity = sales.stream().mapToInt(StockSale::getAmount).sum();

        int remainingStockQuantity = stock.getQuantity();
        double estimatedRemainingSalesRevenue = remainingStockQuantity * stock.getUnitSalePrice();
        double totalExpectedRevenue = oldSalesRevenue + estimatedRemainingSalesRevenue;

        double totalPurchaseCost = entries.stream().mapToDouble(StockEntry::getTotalPricePaid).sum();
        int totalQuantityAdded = entries.stream().mapToInt(StockEntry::getQuantityAdded).sum();
        double averagePurchasePrice = totalQuantityAdded > 0 ? (totalPurchaseCost / totalQuantityAdded) : 0.0;

        double expectedProfit = totalExpectedRevenue - totalPurchaseCost;

        return new ProfitAnalysisReport(
                code,
                oldSalesRevenue,
                oldSalesQuantity,
                remainingStockQuantity,
                estimatedRemainingSalesRevenue,
                totalExpectedRevenue,
                totalPurchaseCost,
                averagePurchasePrice,
                expectedProfit
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RemainingStockItem {
        private String code;
        private int quantity;
        private double unitSalePrice;
        private double averagePurchasePrice;
        private double totalCostValue;
        private double totalSaleValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RemainingStockReport {
        private List<RemainingStockItem> items;
        private double grandTotalCostValue;
        private double grandTotalSaleValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfitAnalysisReport {
        private String code;
        private double oldSalesRevenue;
        private int oldSalesQuantity;
        private int remainingStockQuantity;
        private double estimatedRemainingSalesRevenue;
        private double totalExpectedRevenue;
        private double totalPurchaseCost;
        private double averagePurchasePrice;
        private double expectedProfit;
    }
}
