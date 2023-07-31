package com.tinqin.bff.api.operation.auth.loginUser;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResult implements ProcessorResult {

    private String jwt;
}
