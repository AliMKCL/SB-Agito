package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(
        name = "Product",
        description = "Schema to hold product information"
)
@Data
public class ProductDto {

    @Schema(
            description = "Item code of the product", example = "001"
    )
    @NotEmpty(message = "Code cannot be null or empty.")
    private String code;

    @Schema(
            description = "Name of the product", example = "water"
    )
    @NotEmpty(message = "Name cannot be null or empty.")
    private String name;

    @Schema(
            description = "Category of the product", example = "drink"
    )
    @NotEmpty(message = "Category cannot be null or empty.")
    private String category;

    @Schema(
            description = "Price of the product", example = "5.99"
    )
    @NotEmpty(message = "Price cannot be null or empty.")
    private double price;

    @Schema(
            description = "Stock count of the product", example = "10"
    )
    @NotEmpty(message = "Stock cannot be null or empty.")
    private int stock;
}
