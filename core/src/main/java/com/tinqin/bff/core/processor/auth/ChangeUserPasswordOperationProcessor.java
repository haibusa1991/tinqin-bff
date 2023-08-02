package com.tinqin.bff.core.processor.auth;

import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordInput;
import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordOperation;
import com.tinqin.bff.api.operation.auth.changePassword.ChangeUserPasswordResult;
import com.tinqin.bff.core.exception.CurrentPasswordInvalidException;
import com.tinqin.bff.core.exception.UserNotFoundException;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Security;

@Service
@RequiredArgsConstructor
public class ChangeUserPasswordOperationProcessor implements ChangeUserPasswordOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ChangeUserPasswordResult process(ChangeUserPasswordInput input) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        if (!this.passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new CurrentPasswordInvalidException();
        }

        user.setPassword(this.passwordEncoder.encode(input.getPassword()));
        this.userRepository.save(user);

        return new ChangeUserPasswordResult();
    }
}
