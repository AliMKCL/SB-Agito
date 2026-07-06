package com.staj.stock.controller;

import jakarta.validation.constraints.Null;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IAdminStockController {

    @PostMapping("/admin/addStock")
    ResponseEntity<Null> addStock(@RequestParam String code, @RequestParam int quantity);

    @GetMapping("/admin/checkStock")
    ResponseEntity<Null> checkStock(@RequestParam String code);

    @PostMapping("/admin/removeStock")
    ResponseEntity<Null> removeStock(@RequestParam String code, @RequestParam int quantity);

    @DeleteMapping("/admin/deleteItem")
    ResponseEntity<Null> deleteItem(@RequestParam String code);
}
