package com.tinqin.bff.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserInput;
import com.tinqin.bff.core.exception.InvalidCredentialsException;
import com.tinqin.bff.persistence.repository.InvalidatedTokensRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class JwtManager {
    private final Duration TOKEN_VALIDITY = Duration.of(30, ChronoUnit.DAYS);
//    private final Duration TOKEN_VALIDITY = Duration.of(5, ChronoUnit.SECONDS);

    private final ApplicationUserDetailsService applicationUserDetailsService;
    private final InvalidatedTokensRepository invalidatedTokensRepository;
    private final ApplicationContext context;

    @Value("${jwt-secret}")
    private String jwtSecret;

    public String generateJwt(LoginUserInput input) {

        UserDetails userDetails = applicationUserDetailsService.loadUserByUsername(input.getEmail());

        if (!context.getBean(PasswordEncoder.class).matches(input.getPassword(), userDetails.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return JWT.create()
                .withClaim("email", userDetails.getUsername())
                .withClaim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(TOKEN_VALIDITY))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String getEmail(String jwt) {
        if (this.invalidatedTokensRepository.existsByToken(jwt)) {
            throw new JWTVerificationException("Token blacklisted.");
        }

        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withClaimPresence("email")
                .build()
                .verify(jwt);

        return decoded.getClaim("email").asString();
    }
}
