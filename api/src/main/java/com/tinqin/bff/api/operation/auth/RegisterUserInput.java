package com.tinqin.bff.api.operation.auth;

import com.tinqin.bff.api.base.ProcessorInput;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RegisterUserInput implements ProcessorInput {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
