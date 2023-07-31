package com.tinqin.bff.rest.security;

import com.tinqin.bff.core.auth.ApplicationUserDetailsService;
import com.tinqin.bff.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.formLogin(c -> c
                                .loginPage("/auth/login")
                                .failureHandler(((request, response, exception) -> response.sendError(403, "Invalid credentials")))
                                .successHandler((request, response, authentication) -> response.setStatus(200))

//                        .usernameParameter("email")
                )
                .authorizeHttpRequests(c -> c
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/auth/login").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                )
                .cors(c -> c.disable())
                .csrf(c -> c.disable())
//                .userDetailsService(getApplicationUserDetailsService())
                .build();
    }

    @Bean
    public UserDetailsService getApplicationUserDetailsService(UserRepository userRepository) {
        return new ApplicationUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
