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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public class AdminProductController implements IAdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService){
        this.productService = productService;
    }

    @Operation(
            summary = "Create product endpoint.",
            description = "Endpoint used to create a product in the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Product already exists",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @Override
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){

        productService.createProduct(productDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productDto);

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



    @Operation(
            summary = "Edit product endpoint.",
            description = "Endpoint used to edit a product's details in the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product edited"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))


    })
    @Override
    public ResponseEntity<ProductDto> editProduct(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "001"
            )@RequestParam String code, @RequestBody ProductDto productDto){
        boolean isChanged = productService.editProduct(code, productDto);
        if (isChanged){
            return ResponseEntity.status(HttpStatus.OK).body(productDto);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


    }



    @Operation(
            summary = "Delete product endpoint.",
            description = "Endpoint used to delete a product from the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @Override
    public ResponseEntity<Void> deleteProduct(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "001"
            ) @RequestParam String code) {
        productService.deleteProduct(code);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
