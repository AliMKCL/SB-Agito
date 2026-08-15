package com.agito.staj.controller;

import com.agito.staj.dto.ErrorResponseDto;
import com.agito.staj.dto.ProductDto;
import com.agito.staj.dto.SearchProductDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path="/apiAdmin", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IAdminProductController {

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
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto
    );



    @Operation(
            summary = "Fetch all products endpoint.",
            description = "Endpoint used to fetch all products in the database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products found.",
            content = @Content
    )
    @PostMapping("/fetchAll")
    ResponseEntity<List<ProductDto>> findAllProducts(@Valid @RequestBody SearchProductDto searchProductDto);



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
    @GetMapping("/fetch")
    ResponseEntity<ProductDto> findProduct(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )
            @RequestParam("code")
                @NotBlank(message = "Code cannot be blank")
                @Size(min = 4, max = 4, message = "Code length must be exactly 4")
                String code
    );


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
                    responseCode = "400",
                    description = "Product not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )


    })
    @PutMapping(value="/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductDto> editProduct(
            @Valid @RequestBody ProductDto productDto
    );



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
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteProduct(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )
            @RequestParam("code")
                @NotBlank(message = "Code cannot be blank")
                @Size(min = 4, max = 4, message = "Code length must be exactly 4")
                String code
    );

    @Operation(
            summary = "Seed product database endpoint.",
            description = "Endpoint used to seed the product database with 30 unique products."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product database seeded successfully"
            )
    })
    @PostMapping("/seedDb")
    ResponseEntity<List<ProductDto>> seedDatabase();
}
