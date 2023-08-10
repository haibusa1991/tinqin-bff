package com.tinqin.bff.api.operation.auth.registerUser;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserResult implements ProcessorResult {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private double credit;
}
