package com.staj.stock.service;

import com.staj.stock.entity.Stock;
import com.staj.stock.exception.ItemNotFoundException;
import com.staj.stock.repository.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class StockService {

    private StockRepository stockRepository;

    /**
     *
     * @param code
     */
    public Integer checkStock(String code){
        Stock item = stockRepository.findById(code).orElseThrow(() -> new ItemNotFoundException("Item not found in stock database with code: " + code));
        return item.getQuantity();
    }

    /**
     *
     * @param code
     * @param quantity
     */
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

    /**
     *
     * @param code
     * @param quantity
     */
    @Transactional
    public void removeStock(String code, int quantity){
        Stock item = stockRepository.findById(code)
                .orElseThrow(() -> new ItemNotFoundException("Item not found in stock database with code: " + code));
        item.setQuantity(item.getQuantity() - quantity);
        stockRepository.save(item);
    }

    /**
     *
     * @param code
     */
    @Transactional
    public void deleteItem(String code){
        stockRepository.findById(code).orElseThrow(() -> new ItemNotFoundException("Item not found in stock database with code: " + code));
        stockRepository.deleteById(code);
    }

}



