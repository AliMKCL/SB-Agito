package com.staj.stock.controller.impl;

import com.staj.stock.controller.IStockCalculationController;
import com.staj.stock.entity.Stock;
import com.staj.stock.service.AnalysisService;
import com.staj.stock.service.AnalysisService.RemainingStockReport;
import com.staj.stock.service.AnalysisService.ProfitAnalysisReport;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Validated
@RestController
public class StockCalculationController implements IStockCalculationController {

    private final AnalysisService analysisService;

    @Override
    public ResponseEntity<RemainingStockReport> getRemainingStockValue() {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.getRemainingStockValueReport());
    }

    @Override
    public ResponseEntity<List<Stock>> getLowStockItems() {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.getLowStockItems());
    }

    @Override
    public ResponseEntity<ProfitAnalysisReport> getExpectedProfit(String code) {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.getProductExpectedProfitReport(code));
    }
}
