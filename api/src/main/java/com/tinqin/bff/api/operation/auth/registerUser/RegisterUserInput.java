package com.tinqin.bff.api.operation.auth.registerUser;

import com.tinqin.bff.api.base.ProcessorInput;
import jakarta.validation.constraints.Email;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RegisterUserInput implements ProcessorInput {
    @Email
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
