package org.springframework.social.wechat.api;

import java.time.Instant;

public class MpAccessToken {
    Long id;
    String accessToken;
    long expiresIn;
    long expiresAt;

    public MpAccessToken(String accessToken, long expiresIn, long expiresAt) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public boolean isExpired() {
        return this.expiresAt <= Instant.now().getEpochSecond();
    }

    @Override
    public String toString() {
        return String.format("MpAccessToken{access_token: %s, expires_in: %d}", this.accessToken, this.expiresIn);
    }
}
