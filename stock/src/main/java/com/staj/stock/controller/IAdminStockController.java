package com.staj.stock.controller;

import com.staj.stock.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path="/apiAdmin", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IAdminStockController {

    /**
     *
     * @param code
     * @param quantity
     */
    @Operation(
            summary = "Add stock endpoint.",
            description = "Endpoint used to add stock to an item in the stock database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock added to item"
            )
    })
    @PostMapping("/addStock")
    ResponseEntity addStock(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )@RequestParam String code,
            @Parameter(
                    name = "quantity",
                    description = "The quantity of the product available in stock",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "10"
            )@RequestParam int quantity);

    /**
     *
     * @param code
     */
    @Operation(
            summary = "Check stock endpoint.",
            description = "Endpoint used to check the stock quantity of a product in the stock database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @GetMapping("/checkStock")
    ResponseEntity<Integer> checkStock(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )@RequestParam String code);

    /**
     *
     * @param code
     * @param quantity
     * @return
     */
    @Operation(
            summary = "Remove stock endpoint.",
            description = "Endpoint used to decrease the stock quantity of an item in the stock database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock decreased"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @PostMapping("/removeStock")
    ResponseEntity removeStock(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )@RequestParam String code,
            @Parameter(
                    name = "quantity",
                    description = "The quantity of the product available in stock",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "10"
            )@RequestParam int quantity);

    /**
     *
     * @param code
     */
    @Operation(
            summary = "Delete item endpoint.",
            description = "Endpoint used delete an item from the stock database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @DeleteMapping("/deleteItem")
    ResponseEntity deleteItem(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )@RequestParam String code);
}
