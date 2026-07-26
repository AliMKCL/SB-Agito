package com.staj.stock.controller;

import com.staj.stock.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "Stock Excel Controller",
        description = "Controller to import stock entries from Excel files"
)
@RequestMapping(path="/apiAdmin", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IStockExcelController {

    @Operation(
            summary = "Upload stock entries from Excel",
            description = "Endpoint used to import StockEntry records from an Excel file, updating the overall stock quantities accordingly."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully imported stock from Excel"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or malformed Excel file",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item code not found in stock database",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping(value = "/uploadExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> uploadExcel(
            @Parameter(
                    description = "The Excel file containing StockEntry records. Headers should be: code, quantity, totalPricePaid, vendor.",
                    required = true
            ) @RequestParam("file") MultipartFile file);
}
