package com.agito.staj.mapper;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {

    /**
     * Maps a Category entity to CategoryDto.
     */
    public static CategoryDto CategoryEntityToDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }
        return dto;
    }

    /**
     * Maps a CategoryDto to Category entity with a parent Category.
     */
    public static Category CategoryDtoToEntity(CategoryDto dto, Category parentCategory) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setParent(parentCategory);
        return category;
    }

    /**
     * Maps a list of Category entities to a list of CategoryDtos.
     */
    public static List<CategoryDto> ListCategoryEntityToDto(List<Category> categories) {
        List<CategoryDto> dtoList = new ArrayList<>();
        if (categories == null) {
            return dtoList;
        }
        for (Category category : categories) {
            dtoList.add(CategoryEntityToDto(category));
        }
        return dtoList;
    }
}
