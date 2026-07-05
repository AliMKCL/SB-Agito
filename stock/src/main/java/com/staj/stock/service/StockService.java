package com.staj.stock.service;

import com.staj.stock.dto.ProductDto;
import com.staj.stock.service.client.ProductFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class StockService {

    private ProductFeignClient productFeignClient;

    public Integer checkStock(String code){
       return productFeignClient.findProduct(code).getBody().getStock();
    }

    public void addStock(String code, int quantity){
        ProductDto productDto = productFeignClient.findProduct(code).getBody();
        productDto.setStock(productDto.getStock() + quantity);
        productFeignClient.editProduct(productDto);
    }

    public void removeStock(String code, int quantity){
        ProductDto productDto = productFeignClient.findProduct(code).getBody();
        productDto.setStock(productDto.getStock() - quantity);
        productFeignClient.editProduct(productDto);
    }
}
