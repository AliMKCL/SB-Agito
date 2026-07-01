package com.agito.staj.mapper;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    /**
    Transforms a Product entity to a ProductDto
     */
    public static ProductDto ProductEntityToDto(Product product){
        ProductDto newProductDto = new ProductDto();
        newProductDto.setCode(product.getCode());
        newProductDto.setName(product.getName());
        newProductDto.setCategory(product.getCategory());
        newProductDto.setPrice(product.getPrice());
        newProductDto.setStock(product.getStock());
        return newProductDto;
    }

    /**
        Transforms a ProductDto to a Product entity.
     */
    // Validations + exceptions
    // Category entity oluştur (parent-child olarak bağlantılı kategoriler)
    //  kategori kodu oradan otomatik gelsin user input ile değil
    // Doğrulamasını yap

    public static Product ProductDtoToEntity(ProductDto productDto){
        Product newProduct = new Product();
        newProduct.setCode(productDto.getCode());
        newProduct.setName(productDto.getName());
        newProduct.setCategory(productDto.getCategory());
        newProduct.setPrice(productDto.getPrice());
        newProduct.setStock(productDto.getStock());
        return newProduct;
    }

    /**
        Transforms a list of Product entities to a list of ProductDto's.
     */
    public static List<ProductDto> ListProductEntityToDto(List<Product> products){
        List<ProductDto> dtoList = new java.util.ArrayList<>();

        if (products == null) {
            return dtoList;
        }

        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setCode(product.getCode());
            productDto.setName(product.getName());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setStock(product.getStock());

            dtoList.add(productDto);
        }

        return dtoList;
    }

    /**
        Transforms a list of ProductDto's to a list of Product entities.
     */
    public static List<Product> ListProductDtoToEntity(List<ProductDto> products){
        List<Product> productList = new java.util.ArrayList<>();

        if (products == null) {
            return productList;
        }

        for (ProductDto productDto : products) {
            Product product = new Product();
            product.setCode(productDto.getCode());
            product.setName(productDto.getName());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setStock(productDto.getStock());

            productList.add(product);
        }

        return productList;
    }

}
