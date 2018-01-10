package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.controller.ConcurrentConflictException;
import net.cmlzw.nineteen.domain.Token;
import net.cmlzw.nineteen.domain.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.social.wechat.api.MpAccessToken;
import org.springframework.social.wechat.api.MpAccessTokenRepository;

import java.util.List;

public class SocialMpAccessTokenRepository implements MpAccessTokenRepository {
    @Autowired
    TokenRepository repository;

    @Override
    public MpAccessToken get() {
        Token current = findFirst();
        MpAccessToken result = null;
        if (current != null) {
            result = new MpAccessToken(current.getContent(), current.getExpiresIn(), current.getExpiresAt());
        }
        return result;
    }

    @Override
    public void save(MpAccessToken accessToken) {
        int retry = 0;
        do {
            Token current = findFirst();
            if (current == null) {
                current = new Token(TokenType.MpAccessToken,
                        accessToken.getAccessToken(), accessToken.getExpiresIn(), accessToken.getExpiresAt());
            } else {
                current.setContent(accessToken.getAccessToken());
                current.setExpiresIn(accessToken.getExpiresIn());
                current.setExpiresAt(accessToken.getExpiresAt());
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

    private Token findFirst() {
        Token current = null;
        List<Token> all = repository.findAllByType(TokenType.MpAccessToken);
        if (all != null && all.size() > 0) {
            current = all.get(0);
        }
        return current;
    }
}
