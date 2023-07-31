package com.tinqin.bff.core.processor.auth;

import com.tinqin.bff.api.operation.auth.registerUser.RegisterUserInput;
import com.tinqin.bff.api.operation.auth.registerUser.RegisterUserOperation;
import com.tinqin.bff.api.operation.auth.registerUser.RegisterUserResult;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.bff.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserOperationProcessor implements RegisterUserOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterUserResult process(RegisterUserInput input) {
        User user = User.builder()
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .phoneNumber(input.getPhoneNumber())
                .build();

        User persisted = this.userRepository.save(user);

        return RegisterUserResult.builder()
                .id(persisted.getId())
                .email(persisted.getEmail())
                .firstName(persisted.getFirstName())
                .lastName(persisted.getLastName())
                .phoneNumber(persisted.getPhoneNumber())
                .build();
    }
}
