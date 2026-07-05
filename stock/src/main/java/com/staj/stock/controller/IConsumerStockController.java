package com.staj.stock.controller;

import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface IConsumerStockController {

    @GetMapping("/consumer/checkStock")
    ResponseEntity<Integer> checkStock(@RequestParam String code);

    @PostMapping("/consumer/removeStock")
    ResponseEntity<Null> removeStock(@RequestParam String code, @RequestParam int quantity);

}
