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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public ResponseEntity<RegisterUserResult> registerUser(@RequestBody @Valid RegisterUserInput input) {
        return new ResponseEntity<>(this.registerUser.process(input), HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginUserResult> loginUser(@RequestBody @Valid LoginUserInput input) {
        LoginUserResult result = this.loginUser.process(input);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", result.getJwt());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PatchMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ChangeUserPasswordResult> changePassword(@RequestBody @Valid ChangeUserPasswordInput input) {
        return new ResponseEntity<>(this.changePassword.process(input), HttpStatus.NO_CONTENT);
    }
}