package com.agito.staj.dto;

import com.agito.staj.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(
        name = "Product",
        description = "Schema to hold product information"
)
@NoArgsConstructor
@Data
public class ProductDto implements Serializable {

    @Schema(
            description = "Item code of the product", example = "001"
    )
    @NotEmpty(message = "Code cannot be null or empty.")
    @NotNull
    @Size(min=4, max=4, message="Length of the product code must be exactly 4." )
    private String code;

    @Schema(
            description = "Name of the product", example = "water"
    )
    @NotEmpty(message = "Name cannot be null or empty.")
    @NotNull
    @Size(min=3, max=20, message="Length of product name must be between 5 and 20")
    private String name;

    @Schema(
            description = "Category of the product", example = "drink"
    )
    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @Schema(
            description = "Price of the product", example = "5.99"
    )
    @NotNull(message = "Price cannot be null or empty.")
    @Min(value=0)
    private Double price;
}
