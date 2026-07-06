package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String code;

    @Schema(
            description = "Name of the product", example = "water"
    )
    private String name;

    @Schema(
            description = "Category of the product", example = "drink"
    )
    private String category;
}
