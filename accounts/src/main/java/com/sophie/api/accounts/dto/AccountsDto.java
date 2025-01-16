package com.sophie.api.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
        name = "Accounts",
        description = "Schema to hold Account information"
)
public class AccountsDto {

    @NotEmpty(message = "Account number can not be a null or empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Account number must be 10 digits")
    @Schema(
            description = "Account Number", example = "3454433243"
    )
    private Long accountNumber;

    @NotBlank(message = "Account type can not be a null or empty")
    @Schema(
            description = "Account Number", example = "Savings"
    )
    private String accountType;

    @NotBlank(message = "Branch address can not be a null or empty")
    @Schema(
            description = "Account branch address", example = "123 NewYork"
    )
    private String branchAddress;
}