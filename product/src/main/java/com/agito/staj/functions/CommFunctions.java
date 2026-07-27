package com.agito.staj.functions;

import com.agito.staj.dto.ProdCreateCommDto;
import com.agito.staj.dto.ProdDeleteCommDto;
import com.agito.staj.dto.ProdEditCommDto;
import com.agito.staj.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class CommFunctions {

    private static final Logger log = LoggerFactory.getLogger(CommFunctions.class);

    @Bean
    public Consumer<ProdCreateCommDto> confirmNewProduct(ProductService productService) {
        return prodCreateCommDto -> {
            log.info("PRODUCT_REGISTER_REQUEST");
            productService.updateCommSwitch(prodCreateCommDto.code());
        };
    }

    @Bean
    public Consumer<ProdDeleteCommDto> confirmDeleteProduct(ProductService productService) {
        return prodDeleteCommDto -> {
            log.info("PRODUCT_DELETE_REQUEST");
            productService.deleteProductLocal(prodDeleteCommDto.code());
        };
    }

    @Bean
    public Consumer<ProdEditCommDto> confirmEditProduct(ProductService productService) {
        return prodEditCommDto -> {
            log.info("PRODUCT_EDIT_CONFIRMATION");
            productService.updateCommSwitch(prodEditCommDto.code());
        };
    }
}
