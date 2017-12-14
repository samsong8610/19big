package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.controller.ConcurrentConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.social.wechat.api.MpAccessToken;
import org.springframework.social.wechat.api.MpAccessTokenRepository;

import java.util.List;

public class SocialMpAccessTokenRepository implements MpAccessTokenRepository {
    @Autowired
    net.cmlzw.nineteen.repository.MpAccessTokenRepository repository;

    @Override
    public MpAccessToken get() {
        net.cmlzw.nineteen.domain.MpAccessToken current = findFirst();
        MpAccessToken result = null;
        if (current != null) {
            result = new MpAccessToken(current.getAccessToken(), current.getExpiresIn());
        }
        return result;
    }

    @Override
    public void save(MpAccessToken accessToken) {
        int retry = 0;
        do {
            net.cmlzw.nineteen.domain.MpAccessToken current = findFirst();
            if (current == null) {
                current = new net.cmlzw.nineteen.domain.MpAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
            } else {
                current.setAccessToken(accessToken.getAccessToken());
                current.setExpiresIn(accessToken.getExpiresIn());
            }
            try {
                repository.save(current);
                break;
            } catch (OptimisticLockingFailureException olfe) {
                // ignore
            }
        } while (retry++ < 3);
        if (retry >= 3) {
            throw new ConcurrentConflictException("Save access token conflict");
        }
    }

    private net.cmlzw.nineteen.domain.MpAccessToken findFirst() {
        net.cmlzw.nineteen.domain.MpAccessToken current = null;
        List<net.cmlzw.nineteen.domain.MpAccessToken> all = repository.findAll();
        if (all != null && all.size() > 0) {
            current = all.get(0);
        }
        return current;
    }
}
