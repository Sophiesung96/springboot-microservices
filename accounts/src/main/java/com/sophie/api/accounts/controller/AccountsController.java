package com.sophie.api.accounts.controller;


import com.sophie.api.accounts.constants.AccountsConstants;
import com.sophie.api.accounts.dto.AccountsContactInfoDto;
import com.sophie.api.accounts.dto.CustomerDto;
import com.sophie.api.accounts.dto.ErrorResponseDto;
import com.sophie.api.accounts.dto.ResponseDto;
import com.sophie.api.accounts.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated //to activate the method-level validation
@Tag(name = "CRUD REST APIs for Accounts"
,description = "CRUD REST APIs to CREATE, READ,UPDATE, DELETE account details")
public class AccountsController {

    private final AccountService accountService;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;
    private Environment environment;


    @Autowired
    public AccountsController(AccountService accountService,Environment environment) {
        this.accountService = accountService;
        this.environment = environment;
    }

    @PostMapping("/create")
    @Operation(summary = "create Account REST API",description = "REST API to create new Customer" +
            "and Account")
    @ApiResponses(
            {
            @ApiResponse(
                    responseCode = "201",
                    description =  "HTTP Status CREATED"),
            @ApiResponse(
                    responseCode = "400",
                    description =  "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto) {
        accountService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }


    @GetMapping("/fetch/{mobileNumber}")
    @Operation(
            summary = "Fetch Account Details REST API",
            description = "REST API to fetch Customer &  Account details based on a mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status Not Found",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponseDto.class
                    ))
            )
    })
    public ResponseEntity<CustomerDto> fetchAccountDetails(@PathVariable @Pattern(regexp = "^[0-9]{10}$"
            , message ="Mobile number must be exactly 10 digits." )
                                                               String mobileNumber) {
        CustomerDto customerDto = accountService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }


    @PutMapping("/update")
    @Operation(
            summary = "Update Account Details REST API",
            description = "REST API to update Customer &  Account details based on a account number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status EXPECTATION_FAILED",
                    content=@Content(schema = @Schema(
                            implementation = ErrorResponseDto.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto) {
        boolean isUpdated = accountService.updateAccount(customerDto);
        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }


    @DeleteMapping("/delete/{mobileNumber}")
    @Operation(
            summary = "Delete Account & Customer Details REST API",
            description = "REST API to delete Customer &  Account details based on a mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Http Status Expectation failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<ResponseDto> deleteAccountDetails(@PathVariable @Pattern(regexp = "^[0-9]{10}$",
            message = "Mobile number must be exactly 10 digits.")  String mobileNumber) {
        boolean isDeleted = accountService.deleteAccount(mobileNumber);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
        }
    }

    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }


}
