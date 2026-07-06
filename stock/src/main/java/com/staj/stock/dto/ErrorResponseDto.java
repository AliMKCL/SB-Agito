package com.staj.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(
        name = "ErrorResponse",
        description = "Schema to hold error response information."
)
@Data
@AllArgsConstructor
public class ErrorResponseDto {

    @Schema(
            description = "API path invoked that caused the error.",
            example = "/api/example"

    )
    private String apiPath;

    @Schema(
            description = "Error code of the thrown error.",
            example = "string"
    )
    private HttpStatus errorCode;

    @Schema(
            description = "Error message of the thrown error."
    )
    private String errorMessage;

    @Schema(
            description = "Local time the error occurred at.",
            example = "2026-07-02T18:15:59.556Z"
    )
    private LocalDateTime errorTime;

}
