package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordInput;
import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordOperation;
import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordResult;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserInput;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserOperation;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserResult;
import com.tinqin.bff.api.operation.auth.registerUser.RegisterUserInput;
import com.tinqin.bff.api.operation.auth.registerUser.RegisterUserOperation;
import com.tinqin.bff.api.operation.auth.registerUser.RegisterUserResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final RegisterUserOperation registerUser;
    private final LoginUserOperation loginUser;
    private final ChangeUserPasswordOperation changePassword;

    @PostMapping(path = "/register")
    @ApiResponse(responseCode = "201",description = "User registered successfully.")
    @ApiResponse(responseCode = "400",description = "Invalid field contents.")
    @Operation(description = "Registers a new user.",
            summary = "Registers a new user.")
    public ResponseEntity<RegisterUserResult> registerUser(@RequestBody @Valid RegisterUserInput input) {
        return new ResponseEntity<>(this.registerUser.process(input), HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    @ApiResponse(responseCode = "200",description = "Login successful.")
    @ApiResponse(responseCode = "400",description = "Invalid field contents.")
    @ApiResponse(responseCode = "403",description = "Invalid credentials.")
    @Operation(description = "Checks credentials and returns JWT in response header.",
            summary = "Login with email and password.")
    public ResponseEntity<LoginUserResult> loginUser(@RequestBody @Valid LoginUserInput input) {
        LoginUserResult result = this.loginUser.process(input);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", result.getJwt());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PatchMapping
    @ApiResponse(responseCode = "200",description = "Password changed successfully.")
    @ApiResponse(responseCode = "400",description = "Current or new password is empty.")
    @ApiResponse(responseCode = "403",description = "Current password is invalid or token is blacklisted.")
    @Operation(description = "Changes the password of the currently logged user and ends current session.",
    summary = "Changes current password and invalidates current jwt.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ChangeUserPasswordResult> changePassword(@RequestBody @Valid ChangeUserPasswordInput input) {
        this.changePassword.process(input);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}