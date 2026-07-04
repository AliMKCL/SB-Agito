package com.agito.staj.controller.impl;

import com.agito.staj.controller.IConsumerProductController;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public class ConsumerProductController implements IConsumerProductController {

    private final ProductService productService;

    public ConsumerProductController(ProductService productService){
        this.productService = productService;
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
}
