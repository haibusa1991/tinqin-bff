package com.tinqin.bff.core.processor.auth;

import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordInput;
import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordOperation;
import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordResult;
import com.tinqin.bff.core.exception.CurrentPasswordInvalidException;
import com.tinqin.bff.persistence.entity.Token;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.InvalidatedTokensRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class
ChangeUserPasswordOperationProcessor implements ChangeUserPasswordOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokensRepository invalidatedTokensRepository;

    @Override
    public ChangeUserPasswordResult process(ChangeUserPasswordInput input) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        if (!this.passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new CurrentPasswordInvalidException();
        }

        user.setPassword(this.passwordEncoder.encode(input.getPassword()));
        this.userRepository.save(user);

        this.invalidatedTokensRepository.save(
                Token.builder()
                        .token((String) SecurityContextHolder.getContext().getAuthentication().getDetails())
                        .build()
        );

        return new ChangeUserPasswordResult();
    }
}
