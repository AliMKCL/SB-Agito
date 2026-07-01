package com.agito.staj.controller;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Product;
import com.agito.staj.service.ProductService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controllerlar için interface implementation
// Swagger ekle
// Service interaface yok

@RestController
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){

        productService.createProduct(productDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productDto);

    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<ProductDto>> findAllProducts(){
        List<ProductDto> products = productService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/fetch")
    public ResponseEntity<ProductDto> findProduct(@RequestParam String code){
        ProductDto productDto = productService.find(code);
        return ResponseEntity.status(HttpStatus.FOUND).body(productDto);
    }

    @PutMapping(value="/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> editProduct(@RequestParam String code, @RequestBody ProductDto productDto){
        boolean isChanged = productService.editProduct(code, productDto);
        if (isChanged){
            return ResponseEntity.status(HttpStatus.OK).body(productDto);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


    }

    @DeleteMapping("/delete")
    public ResponseEntity<ProductDto> deleteProduct(@RequestParam String code) {
        ProductDto productDto = productService.find(code);
        boolean isDeleted = productService.deleteProduct(code);

        if (isDeleted){
            return ResponseEntity.status(HttpStatus.OK).body(productDto);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
