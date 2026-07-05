package com.staj.stock.controller;

import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface IAdminStockController {

    @PostMapping("/admin/addStock")
    ResponseEntity<Null> addStock(@RequestParam String code, @RequestParam int quantity);

    @GetMapping("/admin/checkStock")
    ResponseEntity checkStock(@RequestParam String code);

    @PostMapping("/admin/removeStock")
    ResponseEntity<Null> removeStock(@RequestParam String code, @RequestParam int quantity);
}
