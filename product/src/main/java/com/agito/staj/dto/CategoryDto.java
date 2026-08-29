package com.agito.staj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Schema(
        name = "Category",
        description = "Schema to hold category information"
)
public class CategoryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(
            description = "ID of the category", example = "1"
    )
    private Integer id;

    @Schema(
            description = "Name of the category", example = "drink"
    )
    @NotEmpty(message = "{validation.category.name.notEmpty}")
    @NotNull(message = "{validation.category.name.notNull}")
    @Size(min = 3, max = 50, message = "{validation.category.name.size}")
    private String name;

    @Schema(
            description = "Parent category ID", example = "2"
    )
    private Integer parentId;

    public CategoryDto() {
    }

    public CategoryDto(Integer id, String name, Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
