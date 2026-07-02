package com.agito.staj.controller;

import com.agito.staj.dto.ProductDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IAdminProductController {

    @PostMapping(value = "/admin/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto
    );

    @GetMapping("/admin/fetchAll")
    ResponseEntity<List<ProductDto>> findAllProducts();

    @GetMapping("/admin/fetch")
    ResponseEntity<ProductDto> findProduct(
            @RequestParam("code") String code
    );

    @PutMapping(value="/admin/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductDto> editProduct(
            @RequestParam("code") String code,
            @RequestBody ProductDto productDto
    );

    @DeleteMapping("/admin/delete")
    ResponseEntity<Void> deleteProduct(
            @RequestParam("code") String code
    );
}
