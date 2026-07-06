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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IConsumerProductController {

    @Operation(
            summary = "Fetch all products endpoint.",
            description = "Endpoint used to fetch all products in the database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products found.",
            content = @Content
    )
    @GetMapping("/consumer/fetchAll")
    ResponseEntity<List<ProductDto>> findAllProducts(@RequestBody SearchProductDto searchProductDto);



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
    @GetMapping("/consumer/fetch")
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
            String code);
}
