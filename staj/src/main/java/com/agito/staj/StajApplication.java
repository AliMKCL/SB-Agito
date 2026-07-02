package com.agito.staj;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Products microservice API documentation",
				description = "Ecommmerce platform product API documentation"
		)
)
public class StajApplication {

	public static void main(String[] args) {
		SpringApplication.run(StajApplication.class, args);
	}

}
