package com.agito.staj.mapper;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Category;
import com.agito.staj.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    /**
     * @param product
     * @return the ProductDto object created from the fields of the input Product entity product.
     */
    public static ProductDto ProductEntityToDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto newProductDto = new ProductDto();
        newProductDto.setCode(product.getCode());
        newProductDto.setName(product.getName());
        if (product.getCategory() != null) {
            newProductDto.setCategoryId(product.getCategory().getId());
        }
        newProductDto.setPrice(product.getPrice());
        return newProductDto;
    }

    /**
     * @param productDto
     * @param category
     * @return the Product entity product created from the fields of the input ProductDto.
     */
    public static Product ProductDtoToEntity(ProductDto productDto, Category category) {
        if (productDto == null) {
            return null;
        }
        Product newProduct = new Product();
        newProduct.setCode(productDto.getCode());
        newProduct.setName(productDto.getName());
        newProduct.setCategory(category);
        newProduct.setPrice(productDto.getPrice());
        return newProduct;
    }

    /**
     * @param products
     * @return a list of ProductDto's created from the fields of the input list of Product entities.
     */
    public static List<ProductDto> ListProductEntityToDto(List<Product> products) {
        List<ProductDto> dtoList = new ArrayList<>();
        if (products == null) {
            return dtoList;
        }
        for (Product product : products) {
            dtoList.add(ProductEntityToDto(product));
        }
        return dtoList;
    }

    /**
     * @param products
     * @param category
     * @return a list of Product entity objects created from the fields of the input list of ProductDto's.
     */
    public static List<Product> ListProductDtoToEntity(List<ProductDto> products, Category category) {
        List<Product> productList = new ArrayList<>();
        if (products == null) {
            return productList;
        }
        for (ProductDto productDto : products) {
            productList.add(ProductDtoToEntity(productDto, category));
        }
        return productList;
    }
}
