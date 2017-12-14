package org.springframework.social.wechat.api;

public interface MpAccessTokenRepository {
    MpAccessToken get();
    void save(MpAccessToken accessToken);
}
