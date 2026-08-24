package com.staj.stock.validator;

import com.staj.stock.entity.Stock;
import com.staj.stock.exception.ItemNotFoundException;
import com.staj.stock.exception.StockOutOfBoundsException;
import com.staj.stock.util.Translator;

import java.util.Optional;

public class StockValidator {

    public static Stock validateStockExists(Optional<Stock> opt, String code) {
        return opt.orElseThrow(() -> new ItemNotFoundException(Translator.toLocale("error.stock.itemNotFound", code)));
    }

    public static void validateStockNotDuplicate(boolean exists) {
        if (exists) {
            throw new RuntimeException("Should not be possible to reach here, duplicate checked already.");
        }
    }

    public static void validateStockSufficiency(int quantityNeeded, int availableQuantity) {
        if (quantityNeeded > availableQuantity) {
            throw new StockOutOfBoundsException(Translator.toLocale("error.stock.outOfBounds"));
        }
    }

    public static void validateImportData(String code, int quantity) {
        if (code == null || code.isEmpty() || quantity <= 0) {
            throw new IllegalArgumentException(Translator.toLocale("error.stock.excel.invalidRow"));
        }
    }

}
