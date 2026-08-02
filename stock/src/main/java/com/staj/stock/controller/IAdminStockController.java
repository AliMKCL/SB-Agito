package com.staj.stock.controller;

import com.staj.stock.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(path="/apiAdmin", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IAdminStockController {

    /**
     *
     * @param code
     * @param quantity
     * @param unitPrice
     * Endpoint for adding an entry in the stock database automatically, at product creation.
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
    @PostMapping("/addStockAuto")
    ResponseEntity addStockAuto(
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
            )@RequestParam int quantity,
            @Parameter(
                    name = "unitPrice",
                    description = "The price of 1 instance of the product.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "10.0"
            ) @RequestParam double unitPrice);

    /**
     *
     * @param code
     * @param quantity
     * Endpoint for adding stock manually (After entry in the stock table for the item is created).
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
    @PostMapping("/addStockVendor")
    ResponseEntity addStockVendor(
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
            )@RequestParam int quantity,
            @Parameter(
                    name = "totalPricePaid",
                    description = "The total price paid for all the products",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "1800.00")
            @RequestParam double totalPricePaid,
            @Parameter(
                    name = "vendor",
                    description = "The name of the vendor the products were bought from.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "Amazon")
            @RequestParam String vendor);


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


    /**
     *
     * @param code
     */
    @Operation(
            summary = "Edit item endpoint.",
            description = "Endpoint used edit the unitSalePrice of a stock item (only called by the Product service)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item edited"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @PutMapping("/editItem")
    ResponseEntity editItem(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )@RequestParam String code,
            @Parameter(
                    name = "unitPrice",
                    description = "The unit price of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "10.0"
            )@RequestParam double unitPrice);


    @Operation(
            summary = "Manually run stock scheduler endpoint.",
            description = "Endpoint to run the stock scheduler manually."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "StockSchedular ran successfully."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "An error occured.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @GetMapping("/runStockScheduler")
    ResponseEntity runStockScheduler() throws MessagingException, IOException;
}

