package com.agito.staj.controller;

import com.agito.staj.dto.CategoryDto;
import com.agito.staj.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/apiAdmin/Category", produces = {MediaType.APPLICATION_JSON_VALUE})
public interface ICategoryController {

    @Operation(
            summary = "Create category endpoint.",
            description = "Endpoint used to create a category in the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Category already exists / invalid details",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CategoryDto categoryDto
    );

    @Operation(
            summary = "Fetch a singular category endpoint.",
            description = "Endpoint used to fetch a category from the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @GetMapping("/fetch")
    ResponseEntity<CategoryDto> findCategory(
            @Parameter(
                    name = "id",
                    description = "The ID of the category",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "1"
            )
            @RequestParam("id")
            @NotNull(message = "{validation.param.id.notNull}")
            Integer id
    );

    @Operation(
            summary = "Fetch all categories endpoint.",
            description = "Endpoint used to fetch all categories in the database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories found.",
            content = @Content
    )
    @GetMapping("/fetchAll")
    ResponseEntity<List<CategoryDto>> findAllCategories();

    @Operation(
            summary = "Edit category endpoint.",
            description = "Endpoint used to edit a category's details in the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category edited"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category / Name duplicate",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CategoryDto> editCategory(
            @Valid @RequestBody CategoryDto categoryDto
    );

    @Operation(
            summary = "Delete category endpoint.",
            description = "Endpoint used to delete a category from the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteCategory(
            @Parameter(
                    name = "id",
                    description = "The ID of the category",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "1"
            )
            @RequestParam("id")
            @NotNull(message = "{validation.param.id.notNull}")
            Integer id
    );
}
