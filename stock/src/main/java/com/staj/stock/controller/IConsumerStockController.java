package com.staj.stock.controller;

import jakarta.validation.constraints.Null;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IConsumerStockController {

    @GetMapping("/consumer/checkStock")
    ResponseEntity<Integer> checkStock(@RequestParam String code);

    @PostMapping("/consumer/removeStock")
    ResponseEntity<Null> removeStock(@RequestParam String code, @RequestParam int quantity);

}
