package com.tinqin.bff.persistence.repository;

import com.tinqin.bff.persistence.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvalidatedTokensRepository extends JpaRepository<Token, UUID> {

    Boolean existsByToken(String token);
//    Optional<Token> findByToken(String token);

}
