package com.staj.stock.service;

import com.staj.stock.dto.ProdCreateCommDto;
import com.staj.stock.dto.ProdDeleteCommDto;
import com.staj.stock.dto.ProdEditCommDto;
import com.staj.stock.schedulers.StockCheckScheduler;
import com.staj.stock.validator.StockValidator;
import com.staj.stock.entity.Stock;
import com.staj.stock.entity.StockEntry;
import com.staj.stock.entity.StockSale;
import com.staj.stock.exception.ItemNotFoundException;
import com.staj.stock.exception.StockOutOfBoundsException;
import com.staj.stock.repository.StockEntryRepository;
import com.staj.stock.repository.StockRepository;
import com.staj.stock.repository.StockSaleRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.Iterator;

@AllArgsConstructor
@Service
public class StockService {

    private StockRepository stockRepository;

    private StockSaleRepository stockSaleRepository;

    private StockEntryRepository stockEntryRepository;

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StreamBridge streamBridge;

    private final StockCheckScheduler stockCheckScheduler;

    /**
     *
     * @param code
     */
    public Integer checkStock(String code){
        Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);
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
        StockValidator.validateStockNotDuplicate(stockRepository.findById(code).isPresent());
        Stock newStock = new Stock();
        newStock.setCode(code);
        newStock.setQuantity(quantity);
        newStock.setUnitSalePrice(unitPrice);
        newStock.setThreshold(0);
        stockRepository.save(newStock);
        sendCommunication(newStock);
    }

    private void sendCommunication(Stock stock) {
        var prodCreateCommDto = new ProdCreateCommDto(stock.getCode(), stock.getUnitSalePrice());
        log.info("***** Sending ack that product row in stock-db successfully created: {}", prodCreateCommDto);
        var result = streamBridge.send("confirmNewProduct-out-0", prodCreateCommDto);
        log.info("***** Is the Communication request successfully triggered ? : {}", result);
    }

        /**
         *
         * @param code
         * @param quantity
         * For adding stock manually (After entry in the stock table for the item is created).
         */
        @Transactional
        public void addStock(String code, int quantity){
            Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);
            item.setQuantity(item.getQuantity() + quantity);
            stockRepository.save(item);

    }

    @Transactional
    public void addStockVendor(String code, int quantity, double totalPricePaid, String vendor){
        Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);
        if (true){
            StockEntry stockEntry = createStockEntryObject(code, quantity, totalPricePaid, vendor);
            stockEntryRepository.save(stockEntry);
            item.setQuantity(item.getQuantity() + quantity);
            stockRepository.save(item);
        }
    }

    /**
     *
     * @param code
     * @param quantity
     * Admin method of removing stock (does not count as a sale).
     */
    @Transactional
    public void removeStock(String code, int quantity){
        Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);
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
        Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);
        StockValidator.validateStockSufficiency(quantity, item.getQuantity());
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
        StockValidator.validateStockExists(stockRepository.findById(code), code);
        // Delay added manually to simulate a slow stockDB.
        try {
            Thread.sleep(5000); // Artificial delay to test async behavior
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        stockRepository.deleteById(code);
        sendDeleteCommunication(code);
    }

    private void sendDeleteCommunication(String code) {
        var prodDeleteCommDto = new ProdDeleteCommDto(code);
        log.info("***** Sending ack that product row in stock-db successfully deleted: {}", prodDeleteCommDto);
        var result = streamBridge.send("confirmDeleteProduct-out-0", prodDeleteCommDto);
        log.info("***** Is the Delete Communication request successfully triggered ? : {}", result);
    }

    public StockSale createStockSaleObject(String code, int quantity, double unitSalePrice){
        return new StockSale(
                code,
                quantity * unitSalePrice,
                quantity,
                "",
                LocalDateTime.now());

    }

    public StockEntry createStockEntryObject(String code, int quantity, double totalPricePaid, String vendor){
        return new StockEntry(
                code,
                quantity,
                totalPricePaid,
                totalPricePaid / quantity,
                vendor,
                LocalDateTime.now());
    }


    public void editUnitSalePrice(String code, double unitPrice){
        Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);

        item.setUnitSalePrice(unitPrice);
        stockRepository.save(item);
        sendEditCommunication(item);
    }

    @Transactional
    public void editThreshold(String code, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        Stock item = StockValidator.validateStockExists(stockRepository.findById(code), code);
        item.setThreshold(threshold);
        stockRepository.save(item);
    }

    private void sendEditCommunication(Stock stock) {
        var prodEditCommDto = new ProdEditCommDto(stock.getCode(), stock.getUnitSalePrice());
        log.info("***** Sending ack that product row in stock-db successfully edited: {}", prodEditCommDto);
        var result = streamBridge.send("confirmEditProduct-out-0", prodEditCommDto);
        log.info("***** Is the Edit Communication request successfully triggered ? : {}", result);
    }


    /**
     * Imports StockEntry records from an Excel file.
     * Updates corresponding stock quantities.
     */
    @Transactional
    public void importStockFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = validateExcelMetadata(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip 1st row (headers)
            if (rows.hasNext()) {
                rows.next();
            }
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip empty rows
                if (isRowEmpty(currentRow)) {
                    continue;
                }

                Cell cellCode = currentRow.getCell(0);
                Cell cellQuantity = currentRow.getCell(1);
                Cell cellPrice = currentRow.getCell(2);
                Cell cellVendor = currentRow.getCell(3);

                String code = getCellValueAsString(cellCode);
                int quantity = (int) getCellValueAsDouble(cellQuantity, "quantity", currentRow.getRowNum());
                double totalPricePaid = getCellValueAsDouble(cellPrice, "totalPricePaid", currentRow.getRowNum());
                String vendor = getCellValueAsString(cellVendor);

                StockValidator.validateImportData(code, quantity);

                addStockVendor(code, quantity, totalPricePaid, vendor);
            }
        } catch (Exception e) {
            if (e instanceof ItemNotFoundException || e instanceof IllegalArgumentException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }

    /**
     *
     * @param file
     * @throws IOException
     * Validates the metadata and column structure of input excel files.
     * Returns the opened Workbook.
     */
    private Workbook validateExcelMetadata(MultipartFile file) throws IOException {

        // Workbook = Object for Excel files.

        // File empty check
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        // Check file extension (.xlsx or not).
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file format. Only .xlsx allowed.");
        }

        // Spring aspect, filter / interceptor

        // Check file size
        int maxSizeKB = 10;
        long bytes = file.getSize();
        double kilobytes = (double) bytes / 1024;
        if (kilobytes > maxSizeKB){
            throw new FileSizeLimitExceededException("File size is too big.", (long) kilobytes, maxSizeKB);
        }

        // Check if table contains the correct columns.
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        try {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel file has no header row.");
            }

            String col0 = getCellValueAsString(headerRow.getCell(0));
            String col1 = getCellValueAsString(headerRow.getCell(1));
            String col2 = getCellValueAsString(headerRow.getCell(2));
            String col3 = getCellValueAsString(headerRow.getCell(3));

            if (!"Code".equalsIgnoreCase(col0) && !"code".equalsIgnoreCase(col0)) {
                throw new IllegalArgumentException("Invalid column header at index 0. Expected: Code.");
            }
            if (!"Quantity".equalsIgnoreCase(col1) && !"quantity".equalsIgnoreCase(col1)) {
                throw new IllegalArgumentException("Invalid column header at index 1. Expected: Quantity.");
            }
            if (!"Total Price Paid".equalsIgnoreCase(col2) && !"totalPricePaid".equalsIgnoreCase(col2)) {
                throw new IllegalArgumentException("Invalid column header at index 2. Expected: Total Price Paid.");
            }
            if (!"Vendor".equalsIgnoreCase(col3) && !"vendor".equalsIgnoreCase(col3)) {
                throw new IllegalArgumentException("Invalid column header at index 3. Expected: Vendor.");
            }

            return workbook;
        } catch (Exception e) {
            try {
                workbook.close();
            } catch (IOException ex) {
                throw new IOException("Error with closing the Workbook object.");
            }
            throw e;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < 4; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.format("%d", (long) numericValue);
                } else {
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception ex) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private double getCellValueAsDouble(Cell cell, String columnName, int rowIndex) {
        if (cell == null || cell.getCellType() == org.apache.poi.ss.usermodel.CellType.BLANK) {
            throw new IllegalArgumentException(String.format("Row %d: Column '%s' is empty but a numerical value was expected.", rowIndex + 1, columnName));
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String stringVal = cell.getStringCellValue().trim();
                try {
                    return Double.parseDouble(stringVal);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(String.format("Row %d: Expected a numerical value in column '%s', but found text: \"%s\".", rowIndex + 1, columnName, stringVal));
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception ex) {
                    try {
                        String valFormula = cell.getStringCellValue().trim();
                        return Double.parseDouble(valFormula);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(String.format("Row %d: Expected a formula yielding a numerical value in column '%s'.", rowIndex + 1, columnName));
                    }
                }
            default:
                throw new IllegalArgumentException(String.format("Row %d: Expected a numerical value in column '%s', but found cell type: %s.", rowIndex + 1, columnName, cell.getCellType()));
        }
    }

    public void manualRunStockScheduler() throws MessagingException, IOException {
        stockCheckScheduler.checkStockBelowThreshold();
    }


}



