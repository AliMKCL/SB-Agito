package com.staj.stock.mapper;

import com.staj.stock.dto.StockDto;
import com.staj.stock.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    /**
     * Maps a Stock Entity to a StockDto
     */
    public static StockDto StockEntityToDto(Stock stock) {
        if (stock == null) {
            return null;
        }

        StockDto dto = new StockDto();
        dto.setCode(stock.getCode());
        dto.setQuantity(stock.getQuantity());
        return dto;
    }

    /**
     * Maps a StockDto to a Stock Entity
     */
    public static Stock StockDtoToEntity(StockDto dto) {
        if (dto == null) {
            return null;
        }

        Stock stock = new Stock();
        stock.setCode(dto.getCode());
        stock.setQuantity(dto.getQuantity());
        return stock;
    }
}
