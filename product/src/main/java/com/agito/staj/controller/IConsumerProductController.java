package com.agito.staj.controller;

import com.agito.staj.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IConsumerProductController {

    @GetMapping("/consumer/fetchAll")
    ResponseEntity<List<ProductDto>> findAllProducts();

    @GetMapping("/consumer/fetch")
    ResponseEntity<ProductDto> findProduct(
            @RequestParam("code") String code);
}
