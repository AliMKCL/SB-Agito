package com.staj.stock.controller;

import com.staj.stock.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(path="/apiAdmin", produces={MediaType.APPLICATION_JSON_VALUE})
public interface IAdminMailController {

    @Operation(
            summary = "Send email endpoint",
            description = "Manually trigger the scheduled mail sending method."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Mail sent successfully."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "An error occurred.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
    @GetMapping("/send_email")
    String sendEmail(
            @Parameter(
                    name = "to",
                    description = "The mail address to send the email to",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "example@gmail.com"
            )
            @RequestParam @NotBlank(message = "{validation.param.to.notBlank}") @Email(message = "{validation.param.email.invalid}") String to,
            @Parameter(
                    name = "subject",
                    description = "The subject of the mail to send.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "Low stock items"
            )
            @RequestParam @NotBlank(message = "{validation.param.subject.notBlank}") String subject,
            @Parameter(
                    name = "body",
                    description = "The body of the mail to send.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "Items low on stock are: ......"
            )
            @RequestParam @NotBlank(message = "{validation.param.body.notBlank}") String body
    );
}
