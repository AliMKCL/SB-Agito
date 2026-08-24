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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/apiConsumer", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IConsumerStockController {

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
            )@RequestParam @NotBlank(message = "{validation.param.code.notBlank}") @Size(min = 4, max = 4, message = "{validation.param.code.size}") String code);

    /**
     *
     * @param code
     * @param quantity
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
            )@RequestParam @NotBlank(message = "{validation.param.code.notBlank}") @Size(min = 4, max = 4, message = "{validation.param.code.size}") String code,
            @Parameter(
                    name = "quantity",
                    description = "The quantity of the product available in stock",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "10"
            )@RequestParam @Min(value = 1, message = "{validation.param.quantity.min}") int quantity);

}
