package com.sophie.api.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
        name = "Customer",
        description = "Schema to hold Customer and Account information"
)
public class CustomerDto {

    @NotBlank(message = "Name can not be a null or empty")
    @Size(min = 5,max = 30,message = "The length of the customer name should between 5 and 30. ")
    @Schema(
            description = "Name of the customer", example = "John Doe"
    )
    private String name;

    @NotBlank(message = "Email address can not be a null or empty")
    @Email(message = "email address should be a valid value")
    @Schema(description = "Email address of the customer", example = "example@com")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$",message = "Mobile number must be 10 digits")
    @Schema(
            description = "Mobile Number of the customer", example = "9345432123"
    )
    private String mobileNumber;

    @Valid //nested validation
    @Schema(
            description = "Account details of the Customer"
    )
    private AccountsDto accountsDto;
}
