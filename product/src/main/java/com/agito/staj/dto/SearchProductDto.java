package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
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
    @Size(min=4, max=4, message="{validation.search.code.size}" )
    @Nullable
    private String code;

    @Schema(
            description = "Name of the product", example = "water"
    )
    @Size(min=2, max=20, message="{validation.search.name.size}")
    @Nullable
    private String name;

    @Schema(
            description = "Category of the product", example = "drink"
    )
    @Nullable
    private Integer categoryId;

    @Schema(
            description = "Price filter for the product. Must start with '<' or '>' followed by a numeric value.",
            example = ">100.0"
    )
    @Pattern(
            regexp = "^[<>]\\d+(\\.\\d+)?$",
            message = "{validation.search.price.pattern}"
    )
    @Nullable
    private String price;
}
