package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Token;
import net.cmlzw.nineteen.domain.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByType(TokenType type);
}
