package net.cmlzw.nineteen.domain;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Token {
    @Id
    @GeneratedValue
    Long id;
    @Column(length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    TokenType type;
    @Column(length = 512, nullable = false, unique = true)
    String content;
    long expiresIn;
    long expiresAt;
    @Version
    long version;

    public Token() {}
    public Token(TokenType type, String content, long expiresIn, long expiresAt) {
        this.type = type;
        this.content = content;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isExpired() {
        return this.expiresAt <= Instant.now().getEpochSecond();
    }

    @Override
    public String toString() {
        return String.format("Token{type: %s, content: %s, expires_at: %d, age: %d}",
                this.type, this.content, this.expiresAt, this.expiresIn);
    }
}
