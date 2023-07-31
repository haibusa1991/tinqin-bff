package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.auth.RegisterUserInput;
import com.tinqin.bff.api.operation.auth.RegisterUserOperation;
import com.tinqin.bff.api.operation.auth.RegisterUserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserOperation registerUser;

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterUserResult> registerUser(@RequestBody RegisterUserInput input) {
        return new ResponseEntity<>(this.registerUser.process(input), HttpStatus.CREATED);
    }
}
