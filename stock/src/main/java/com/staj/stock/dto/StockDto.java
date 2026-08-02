package com.staj.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Schema(
            description = "Item code of the product", example = "001"
    )
    @NotEmpty(message = "Code cannot be null or empty.")
    @NotNull
    @Size(min=4, max=4, message="Length of the product code must be exactly 4." )
    private String code;


    @Schema(
            description = "Quantity of the item.", example = "102"
    )
    @NotEmpty(message = "Quantity cannot be null or empty.")
    @NotNull(message = "Quantity cannot be null or empty.")
    @Min(value=0)
    private int quantity;
}
