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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public class ConsumerProductController implements IConsumerProductController {

    private final ProductService productService;

    public ConsumerProductController(ProductService productService){
        this.productService = productService;
    }

    @Operation(
            summary = "Fetch all products endpoint.",
            description = "Endpoint used to fetch all products in the database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products found.",
            content = @Content
    )
    @Override
    public ResponseEntity<List<ProductDto>> findAllProducts(){
        List<ProductDto> products = productService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }



    @Operation(
            summary = "Fetch a singular product endpoint.",
            description = "Endpoint used to fetch a product from the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))

    })
    @Override
    public ResponseEntity<ProductDto> findProduct(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "001"
            )@RequestParam String code){
        ProductDto productDto = productService.find(code);
        return ResponseEntity.status(HttpStatus.FOUND).body(productDto);
    }
}
