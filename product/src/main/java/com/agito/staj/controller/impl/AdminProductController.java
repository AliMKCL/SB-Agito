package com.agito.staj.controller.impl;

import com.agito.staj.controller.IAdminProductController;
import com.agito.staj.dto.ErrorResponseDto;
import com.agito.staj.dto.ProductDto;
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
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public class AdminProductController implements IAdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService){
        this.productService = productService;
    }


    @Override
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        productService.createProduct(productDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productDto);

    }

    @Override
    public ResponseEntity<List<ProductDto>> findAllProducts(){
        List<ProductDto> products = productService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Override
    public ResponseEntity<ProductDto> findProduct(@RequestParam String code){
        ProductDto productDto = productService.find(code);
        return ResponseEntity.status(HttpStatus.FOUND).body(productDto);
    }


    @Override
    public ResponseEntity<ProductDto> editProduct(@RequestParam String code,
                                                  @RequestBody ProductDto productDto){
        boolean isChanged = productService.editProduct(code, productDto);
        if (isChanged){
            return ResponseEntity.status(HttpStatus.OK).body(productDto);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @Override
    public ResponseEntity<Void> deleteProduct(@RequestParam String code) {
        productService.deleteProduct(code);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
