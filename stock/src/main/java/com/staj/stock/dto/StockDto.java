package com.staj.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Currently unused since controller does not do anything with a StockDto object (take input or return).
 * Kept in case it may be useful. (Similarly with StockMapper.
 */
@Schema(
        name = "Stock",
        description = "Schema to hold stock information of a product"
)
@Data
public class StockDto {

    private String code;

    private int quantity;
}
