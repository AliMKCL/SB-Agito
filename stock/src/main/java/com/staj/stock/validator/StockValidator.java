package com.staj.stock.validator;

import com.staj.stock.entity.Stock;
import com.staj.stock.exception.ItemNotFoundException;
import com.staj.stock.exception.StockOutOfBoundsException;

import java.util.Optional;

public class StockValidator {

    public static Stock validateStockExists(Optional<Stock> opt, String code) {
        return opt.orElseThrow(() -> new ItemNotFoundException("Item not found in stock database with code: " + code));
    }

    public static void validateStockNotDuplicate(boolean exists) {
        if (exists) {
            throw new RuntimeException("Should not be possible to reach here, duplicate checked already.");
        }
    }

    public static void validateStockSufficiency(int quantityNeeded, int availableQuantity) {
        if (quantityNeeded > availableQuantity) {
            throw new StockOutOfBoundsException("Not enough stock available");
        }
    }

    public static void validateImportData(String code, int quantity) {
        if (code == null || code.isEmpty() || quantity <= 0) {
            throw new IllegalArgumentException("Invalid row data: code cannot be empty and quantity must be greater than 0.");
        }
    }
}
