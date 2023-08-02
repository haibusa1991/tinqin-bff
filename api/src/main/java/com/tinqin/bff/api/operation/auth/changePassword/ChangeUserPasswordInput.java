package com.tinqin.bff.api.operation.auth.changePassword;

import com.tinqin.bff.api.base.ProcessorInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChangeUserPasswordInput implements ProcessorInput {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String password;
}
