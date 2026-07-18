package com.staj.stock.controller.impl;

import com.staj.stock.controller.IConsumerStockController;
import com.staj.stock.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerStockController implements IConsumerStockController {

    private StockService stockService;

    public ConsumerStockController(StockService stockService){
        this.stockService = stockService;
    }


    @Override
    public ResponseEntity<Integer> checkStock(String code) {
        return ResponseEntity.status(HttpStatus.OK).body(stockService.checkStock(code));
    }

    @Override
    public ResponseEntity<Void> removeStock(String code, int quantity) {
        stockService.removeSoldStock(code, quantity);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
