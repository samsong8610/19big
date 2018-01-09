package org.springframework.social.wechat.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.wechat.WeChatOAuth2Template;
import org.springframework.social.wechat.api.WeChat;
import org.springframework.social.wechat.api.impl.WeChatTemplate;

public class WeChatServiceProvider extends AbstractOAuth2ServiceProvider<WeChat> {
    private static final Logger logger = LoggerFactory.getLogger(WeChatServiceProvider.class);
    public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    public static final String QRCONNECT_URL = "https://open.weixin.qq.com/connect/qrconnect";
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    public static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    public WeChatServiceProvider(String appId, String appSecret) {
        super(createOAuth2Template(appId, appSecret));
    }

    private static OAuth2Operations createOAuth2Template(String appId, String appSecret) {
        WeChatOAuth2Template oAuth2Template = new WeChatOAuth2Template(appId, appSecret,
                AUTHORIZE_URL, ACCESS_TOKEN_URL, REFRESH_TOKEN_URL);
        oAuth2Template.setUseParametersForClientAuthentication(true);
        oAuth2Template.setQRConnectUrl(QRCONNECT_URL);
        return oAuth2Template;
    }

    @Override
    public WeChat getApi(String accessToken) {
        // TODO: remove test code
        if (accessToken == null) {
            throw new RuntimeException("get api without access token");
        }
        logger.debug("getApi with accessToken: " + accessToken);
        return new WeChatTemplate(accessToken);
    }
}
