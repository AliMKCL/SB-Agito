package com.staj.stock.service;

import com.staj.stock.entity.Stock;
import com.staj.stock.entity.StockSale;
import com.staj.stock.exception.ItemNotFoundException;
import com.staj.stock.exception.StockOutOfBoundsException;
import com.staj.stock.repository.StockRepository;
import com.staj.stock.repository.StockSaleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class StockService {

    private StockRepository stockRepository;

    private StockSaleRepository stockSaleRepository;

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
     * For adding an entry in the stock database automatically, at product creation.
     */
    @Transactional
    public void addStockCreate(String code, int quantity, double unitPrice) {
        if (stockRepository.findById(code).isPresent()) {
            throw new RuntimeException("Should not be possible to reach here, duplicate checked already.");
        }
        Stock newStock = new Stock();
        newStock.setCode(code);
        newStock.setQuantity(quantity);
        newStock.setUnitSalePrice(unitPrice);
        stockRepository.save(newStock);
    }


        /**
         *
         * @param code
         * @param quantity
         * For adding stock manually (After entry in the stock table for the item is created).
         */
        @Transactional
        public void addStock(String code, int quantity){
            Optional<Stock> item = stockRepository.findById(code);
            if (item.isPresent()){
                item.get().setQuantity(item.get().getQuantity() + quantity);
                stockRepository.save(item.get());
            }
            else {
                throw new ItemNotFoundException("No item found with code: " + code);
            };

    }

    /**
     *
     * @param code
     * @param quantity
     * Admin method of removing stock (does not count as a sale).
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
     * @param quantity
     * Consumer method of removing stock (counts as a sale)
     */
    @Transactional
    public void removeSoldStock(String code, int quantity){
        Stock item = stockRepository.findById(code)
                .orElseThrow(() -> new ItemNotFoundException("Item not found in stock database with code: " + code));
        if (quantity > item.getQuantity()){
            throw new StockOutOfBoundsException(("Not enough stock available"));
        }
        item.setQuantity(item.getQuantity() - quantity);

        StockSale stockSale = createStockSaleObject(code, quantity, item.getUnitSalePrice());
        stockSaleRepository.save(stockSale);
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

    public StockSale createStockSaleObject(String code, int quantity, double unitSalePrice){
        return new StockSale(
                code = code,
                quantity * unitSalePrice,
                quantity,
                "",
                LocalDateTime.now());

    }


    public void editUnitSalePrice(String code, double unitPrice){
        Optional<Stock> item = stockRepository.findById(code);

        if (item.isEmpty()){
            throw new ItemNotFoundException("Item not found in stock database with code: " + code);
        }

        item.get().setUnitSalePrice(unitPrice);
        stockRepository.save(item.get());
    }

}



