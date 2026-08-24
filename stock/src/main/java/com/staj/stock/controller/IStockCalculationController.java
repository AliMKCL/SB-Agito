package com.staj.stock.controller;

import com.staj.stock.entity.Stock;
import com.staj.stock.dto.ErrorResponseDto;
import com.staj.stock.service.AnalysisService.RemainingStockReport;
import com.staj.stock.service.AnalysisService.ProfitAnalysisReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(path="/apiAnalyst", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IStockCalculationController {

    @Operation(
            summary = "Get remaining stock value.",
            description = "Calculates the total cost value and sale value of the remaining stock per product code, alongside a grand total."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Remaining stock value calculated successfully"
            )
    })
    @GetMapping("/remainingStockValue")
    ResponseEntity<RemainingStockReport> getRemainingStockValue();

    @Operation(
            summary = "Get items low on stock.",
            description = "Retrieves a list of stock items whose quantity is below their own configured thresholds."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of low stock items returned"
            )
    })
    @GetMapping("/lowStockItems")
    ResponseEntity<List<Stock>> getLowStockItems();

    @Operation(
            summary = "Get expected profit report.",
            description = "Calculates expected profit if stock is finished for a product code, including historical sales, remaining sale value, and weighted average entry price."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Expected profit analysis calculated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @GetMapping("/expectedProfit")
    ResponseEntity<ProfitAnalysisReport> getExpectedProfit(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product to analyze",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            ) @RequestParam(name = "code") @NotBlank(message = "{validation.param.code.notBlank}") @Size(min = 4, max = 4, message = "{validation.param.code.size}") String code);
}
