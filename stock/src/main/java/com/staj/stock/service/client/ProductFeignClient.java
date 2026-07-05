package com.staj.stock.service.client;

import com.staj.stock.dto.ProductDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("product")
public interface ProductFeignClient {
    @PutMapping(value="/api/admin/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductDto> editProduct(
            @Valid @RequestBody ProductDto productDto
    );


    @GetMapping("/api/admin/fetch")
    ResponseEntity<ProductDto> findProduct(
            @Parameter(
                    name = "code",
                    description = "The unique code of the product",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "0001"
            )
            @RequestParam("code")
            @NotBlank(message = "Code cannot be blank")
            @Size(min = 4, max = 4, message = "Code length must be exactly 4")
            String code
    );
}
