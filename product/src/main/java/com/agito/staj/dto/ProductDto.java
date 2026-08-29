package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Schema(
        name = "Product",
        description = "Schema to hold product information"
)
public class ProductDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(
            description = "Item code of the product", example = "001"
    )
    @NotEmpty(message = "{validation.product.code.notEmpty}")
    @NotNull(message = "{validation.product.code.notNull}")
    @Size(min=4, max=4, message="{validation.product.code.size}" )
    private String code;

    @Schema(
            description = "Name of the product", example = "water"
    )
    @NotEmpty(message = "{validation.product.name.notEmpty}")
    @NotNull(message = "{validation.product.name.notNull}")
    @Size(min=3, max=20, message="{validation.product.name.size}")
    private String name;

    @Schema(
            description = "Category of the product", example = "drink"
    )
    @NotNull(message = "{validation.product.categoryId.notNull}")
    private Integer categoryId;

    @Schema(
            description = "Price of the product", example = "5.99"
    )
    @NotNull(message = "{validation.product.price.notNull}")
    @Min(value=0, message = "{validation.product.price.min}")
    private Double price;

    public ProductDto() {
    }

    public ProductDto(String code, String name, Integer categoryId, Double price) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
