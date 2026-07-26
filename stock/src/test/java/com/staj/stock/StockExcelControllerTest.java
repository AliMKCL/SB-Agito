package com.staj.stock;

import com.staj.stock.controller.impl.StockExcelController;
import com.staj.stock.entity.Stock;
import com.staj.stock.repository.StockEntryRepository;
import com.staj.stock.repository.StockRepository;
import com.staj.stock.service.StockService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StockExcelControllerTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockEntryRepository stockEntryRepository;

    private StockExcelController stockExcelController;

    @BeforeEach
    void setUp() {
        stockExcelController = new StockExcelController(stockService);

        stockRepository.deleteAll();
        stockEntryRepository.deleteAll();

        // Prepare test stock item
        Stock stock1 = new Stock();
        stock1.setCode("0001");
        stock1.setQuantity(10);
        stock1.setUnitSalePrice(20.0);
        stockRepository.save(stock1);
    }

    @Test
    void testUploadExcel_Success() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stock Entries");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("code");
        header.createCell(1).setCellValue("quantity");
        header.createCell(2).setCellValue("totalPricePaid");
        header.createCell(3).setCellValue("vendor");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("0001");
        dataRow.createCell(1).setCellValue(5);
        dataRow.createCell(2).setCellValue(80.0);
        dataRow.createCell(3).setCellValue("Test Vendor");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] bytes = bos.toByteArray();
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "stock_entries.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes
        );

        ResponseEntity<Void> response = stockExcelController.uploadExcel(file);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Stock updatedStock = stockRepository.findById("0001").orElseThrow();
        assertEquals(15, updatedStock.getQuantity());
    }

    @Test
    void testUploadExcel_ItemNotFound() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stock Entries");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("code");
        header.createCell(1).setCellValue("quantity");
        header.createCell(2).setCellValue("totalPricePaid");
        header.createCell(3).setCellValue("vendor");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("9999");
        dataRow.createCell(1).setCellValue(5);
        dataRow.createCell(2).setCellValue(80.0);
        dataRow.createCell(3).setCellValue("Test Vendor");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] bytes = bos.toByteArray();
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "stock_entries.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes
        );

        assertThrows(RuntimeException.class, () -> {
            stockExcelController.uploadExcel(file);
        });
    }
}
