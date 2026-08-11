package com.staj.stock.controller.impl;

import com.staj.stock.controller.IStockExcelController;
import com.staj.stock.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class StockExcelController implements IStockExcelController {

    private final StockService stockService;

    public StockExcelController(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public ResponseEntity<Void> uploadExcel(MultipartFile file) throws IOException {
        stockService.importStockFromExcel(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
