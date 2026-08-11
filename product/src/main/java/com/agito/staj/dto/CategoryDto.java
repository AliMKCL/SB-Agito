package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(
        name = "Category",
        description = "Schema to hold category information"
)
@NoArgsConstructor
@Data
public class CategoryDto implements Serializable {

    @Schema(
            description = "ID of the category", example = "1"
    )
    private Integer id;

    @Schema(
            description = "Name of the category", example = "drink"
    )
    @NotEmpty(message = "Name cannot be null or empty.")
    @NotNull
    @Size(min = 3, max = 50, message = "Length of category name must be between 3 and 50")
    private String name;

    @Schema(
            description = "Parent category ID", example = "2"
    )
    private Integer parentId;
}
