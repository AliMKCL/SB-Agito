package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(
        name = "SearchProduct",
        description = "Schema to hold SearchProduct information that is provided during the fetchAll endpoint."
)
public class SearchProductDto {
    @Schema(
            description = "Code of the product", example = "001"
    )
    @Size(min=4, max=4, message="Length of the product code must be exactly 4." )
    private String code;

    @Schema(
            description = "Name of the product", example = "water"
    )
    @Size(min=3, max=20, message="Length of product name must be between 5 and 20")
    private String name;

    @Schema(
            description = "Category of the product", example = "drink"
    )
    private String category;
}
