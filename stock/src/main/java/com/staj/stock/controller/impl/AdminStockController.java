package com.staj.stock.controller.impl;

import com.staj.stock.controller.IAdminStockController;
import com.staj.stock.service.StockService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminStockController implements IAdminStockController  {

    private StockService stockService;

    public AdminStockController(StockService stockService){
        this.stockService = stockService;
    }

    @Override
    public ResponseEntity addStockAuto(String code, int quantity, double unitPrice) {
        stockService.addStockCreate(code, quantity, unitPrice);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity addStock(String code, int quantity) {
        stockService.addStock(code, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity addStockVendor(String code, int quantity, double totalPricePaid, String vendor) {
        stockService.addStockVendor(code, quantity, totalPricePaid, vendor);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<Integer> checkStock(String code) {
        return ResponseEntity.status(HttpStatus.OK).body(stockService.checkStock(code));
    }

    @Override
    public ResponseEntity removeStock(String code, int quantity) {
        stockService.removeStock(code, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity deleteItem(String code) {
        stockService.deleteItem(code);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity editItem(String code, double unitPrice) {
        stockService.editUnitSalePrice(code, unitPrice);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
