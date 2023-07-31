package com.tinqin.bff.core.processor.auth;

import com.tinqin.bff.api.operation.auth.loginUser.LoginUserInput;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserOperation;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserResult;
import com.tinqin.bff.core.auth.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserOperationProcessor implements LoginUserOperation {
    private final JwtManager jwtManager;

    @Override
    public LoginUserResult process(LoginUserInput input) {
        return LoginUserResult.builder().jwt(this.jwtManager.generateJwt(input)).build();
    }
}
