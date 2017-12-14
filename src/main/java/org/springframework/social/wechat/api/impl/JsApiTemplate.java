package org.springframework.social.wechat.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.wechat.api.JsApiOperations;
import org.springframework.social.wechat.api.JsApiTicket;
import org.springframework.social.wechat.api.MpAccessToken;
import org.springframework.social.wechat.api.WeChatException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class JsApiTemplate implements JsApiOperations {
    private static final Logger logger = LoggerFactory.getLogger(JsApiTemplate.class);
    private static final String JSAPI_ENDPOINT = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    private MpAccessTokenManager accessTokenManager;
    private RestTemplate restTemplate;

    public JsApiTemplate(MpAccessTokenManager accessTokenManager) {
        this.accessTokenManager = accessTokenManager;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public JsApiTicket getTicket() {
        String jsapiUrl = getJsApiUrl();
        int retry = 0;
        do {
            Map body = restTemplate.getForObject(jsapiUrl, Map.class);
            int errcode = (int) body.getOrDefault("errcode", 0);
            String errmsg = (String) body.getOrDefault("errmsg", "");
            if (errcode != 0) {
                logger.warn("Get JsApi ticket failed: %s(%s)", errmsg, errcode);
                if (errcode == 40014) {
                    // invalid access token, refresh token
                    jsapiUrl = getJsApiUrl();
                }
            } else {
                try {
                    String ticket = body.get("ticket").toString();
                    int expiresIn = (int) body.get("expires_in");
                    JsApiTicket jsApiTicket = new JsApiTicket(ticket, expiresIn);
                    logger.debug(String.format("Got JsApi ticket: %s, expires in %d seconds",
                            jsApiTicket.getTicket(), jsApiTicket.getExpiresIn()));
                    return jsApiTicket;
                } catch (Exception e) {
                    logger.warn("Parse JsApiTicket failed", e);
                }
            }
        } while(retry++ < 3);
        throw new WeChatException(-1, "Get JsApi ticket failed after 3 times retry");
    }

    private String getJsApiUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(JSAPI_ENDPOINT);
        builder.queryParam("access_token", this.accessTokenManager.getAccessToken().getAccessToken());
        builder.queryParam("type", "jsapi");
        return builder.toUriString();
    }
}
