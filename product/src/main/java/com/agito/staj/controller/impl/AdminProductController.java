package com.agito.staj.controller.impl;

import com.agito.staj.controller.IAdminProductController;
import com.agito.staj.dto.ErrorResponseDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
import com.agito.staj.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class AdminProductController implements IAdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService){
        this.productService = productService;
    }


    @Override
    public ResponseEntity<ProductDto> createProduct(ProductDto productDto){
        productService.createProduct(productDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productDto);

    }

    @Override
    public ResponseEntity<List<ProductDto>> findAllProducts(SearchProductDto searchProductDto){
        List<ProductDto> products = productService.findAll(searchProductDto);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Override
    public ResponseEntity<ProductDto> findProduct(String code){
        ProductDto productDto = productService.find(code);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);
    }


    @Override
    public ResponseEntity<ProductDto> editProduct(ProductDto productDto){
        productService.editProduct(productDto);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);
    }


    @Override
    public ResponseEntity<Void> deleteProduct(String code) {
        productService.deleteProduct(code);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<List<ProductDto>> seedDatabase() {
        List<ProductDto> seeded = productService.seedDatabase();
        return ResponseEntity.status(HttpStatus.CREATED).body(seeded);
    }
}
