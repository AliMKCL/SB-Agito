package com.agito.staj.controller;

import com.agito.staj.dto.ProductDto;
import com.agito.staj.entity.Product;
import com.agito.staj.mapper.ProductMapper;
import com.agito.staj.service.IProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
// Controllerlar için interface implementation
// Swagger ekle
// Service interaface yok

@RestController
@RequestMapping(path="/api", produces={MediaType.APPLICATION_JSON_VALUE})
public class ProductController {

    private final IProductService iProductService;
    private final ProductMapper productMapper;

    public ProductController(IProductService iProductService, ProductMapper productMapper){
        this.iProductService = iProductService;
        this.productMapper = productMapper;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){

        Product product = productMapper.ProductDtoToEntity(productDto);
        Product newProduct = iProductService.createProduct(product); //Dto at
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.ProductEntityToDto(newProduct));

    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<ProductDto>> findAllProducts(){
        List<Product> products = iProductService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(productMapper.ListProductEntityToDto(products));
    }

    @GetMapping("/fetch")
    public ResponseEntity<ProductDto> findProduct(@RequestParam String name){
        Product product = iProductService.find(name);
        return ResponseEntity.status(HttpStatus.FOUND).body(productMapper.ProductEntityToDto(product));
    }

    @PutMapping(value="/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> editProduct(@RequestParam String name, @RequestBody ProductDto productDto){
        Product product = productMapper.ProductDtoToEntity(productDto);
        iProductService.editProduct(name, product);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<ProductDto> deleteProduct(@RequestParam String name) {
        Product product = iProductService.find(name);
        iProductService.deleteProduct(name);
        return ResponseEntity.status(HttpStatus.OK).body(productMapper.ProductEntityToDto(product));
    }
}
