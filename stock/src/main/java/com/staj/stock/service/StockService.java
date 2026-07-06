package com.staj.stock.service;

import com.staj.stock.entity.Stock;
import com.staj.stock.repository.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class StockService {

    private StockRepository stockRepository;

    public Integer checkStock(String code){
        return stockRepository.getReferenceById(code).getQuantity();
    }

    @Transactional
    public void addStock(String code, int quantity){
        Optional<Stock> item = stockRepository.findById(code);
        if (item.isPresent()){
            item.get().setQuantity(item.get().getQuantity() + quantity);
            stockRepository.save(item.get());
        }
        else {
            Stock newStock = new Stock();
            newStock.setCode(code);
            newStock.setQuantity(0);
            stockRepository.save(newStock);
        };

    }

    @Transactional
    public void removeStock(String code, int quantity){
        Stock item = stockRepository.findById(code)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Stock not found for code: " + code));
        item.setQuantity(item.getQuantity() - quantity);
        stockRepository.save(item);
    }

    @Transactional
    public void deleteItem(String code){
        if (stockRepository.findById(code).isPresent()){
            stockRepository.deleteById(code);
        }

    }


}
