package com.agito.staj.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("stock")
public interface StockFeignClient {

    // addStock also adds the item if not present, so it is used instead of a new CreateItem method.
    @PostMapping("/apiAdmin/addStockAuto")
    ResponseEntity<Void> addStockAuto(@RequestParam String code, @RequestParam int quantity, @RequestParam double unitPrice);

    @DeleteMapping("/apiAdmin/deleteItem")
    ResponseEntity<Void> deleteItem(@RequestParam String code);
}
