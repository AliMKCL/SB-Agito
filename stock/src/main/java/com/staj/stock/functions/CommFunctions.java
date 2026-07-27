package com.staj.stock.functions;

import com.staj.stock.dto.ProdCreateCommDto;
import com.staj.stock.dto.ProdDeleteCommDto;
import com.staj.stock.dto.ProdEditCommDto;
import com.staj.stock.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class CommFunctions {

    private static final Logger log = LoggerFactory.getLogger(CommFunctions.class);

    @Bean
    public Consumer<ProdCreateCommDto> createNewProduct(StockService stockService){
        return prodCreateCommDto -> {
            log.info("Received new product's data");
            stockService.addStockCreate(prodCreateCommDto.code(),0, prodCreateCommDto.price());
        };
    }

    @Bean
    public Consumer<ProdDeleteCommDto> deleteProduct(StockService stockService){
        return prodDeleteCommDto -> {
            log.info("Received product delete request");
            stockService.deleteItem(prodDeleteCommDto.code());
        };
    }

    @Bean
    public Consumer<ProdEditCommDto> editProduct(StockService stockService){
        return prodEditCommDto -> {
            log.info("Received product edit request");
            stockService.editUnitSalePrice(prodEditCommDto.code(), prodEditCommDto.price());
        };
    }
}
