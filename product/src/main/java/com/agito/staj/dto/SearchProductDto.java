package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    public SearchProductDto() {
    }

    public SearchProductDto(String code, String name, Integer categoryId, String price) {
        this.code = code;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
