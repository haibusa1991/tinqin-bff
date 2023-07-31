package com.tinqin.bff.api.operation.auth.loginUser;

import com.tinqin.bff.api.base.ProcessorInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LoginUserInput implements ProcessorInput {
    @Email
    private String email;
    @NotEmpty
    private String password;
}
