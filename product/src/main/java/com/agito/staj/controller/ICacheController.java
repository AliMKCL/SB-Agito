package com.agito.staj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping(path="/apiCache", produces={MediaType.APPLICATION_JSON_VALUE})
public interface ICacheController {

    @Operation(
            summary = "Get active cache keys",
            description = "Endpoint used to get all active cache keys in Redis matching the product prefix pattern."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Keys retrieved successfully",
            content = @Content
    )
    @GetMapping("/keys")
    ResponseEntity<Set<String>> getCacheKeys();

    @Operation(
            summary = "View cache value",
            description = "Endpoint used to view the raw value of a specific cache key."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cache value retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cache key not found",
                    content = @Content
            )
    })
    @GetMapping("/value")
    ResponseEntity<String> getCacheValue(
            @Parameter(
                    name = "key",
                    description = "The fully qualified cache key (e.g. products::0001)",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "products::0001"
            )
            @RequestParam("key")
            @NotBlank(message = "{validation.param.key.notBlank}")
            String key
    );

    @Operation(
            summary = "Delete cache key",
            description = "Endpoint used to manually remove a key from the Redis cache."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cache key deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cache key not found",
                    content = @Content
            )
    })
    @DeleteMapping("/clear")
    ResponseEntity<String> deleteCacheKey(
            @Parameter(
                    name = "key",
                    description = "The fully qualified cache key to delete",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "products::0001"
            )
            @RequestParam("key")
            @NotBlank(message = "{validation.param.key.notBlank}")
            String key
    );
}
