package org.springframework.social.wechat.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.wechat.api.JsApiOperations;
import org.springframework.social.wechat.api.JsApiTicket;
import org.springframework.social.wechat.api.MpAccessToken;
import org.springframework.social.wechat.api.WeChatException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Map;

public class JsApiTemplate implements JsApiOperations {
    private static final Logger logger = LoggerFactory.getLogger(JsApiTemplate.class);
    private static final String JSAPI_ENDPOINT = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    private MpAccessTokenManager accessTokenManager;
    private RestTemplate restTemplate;
    private JsApiTicket ticket = null;
    private long expiresAt = 0;

    public JsApiTemplate(MpAccessTokenManager accessTokenManager) {
        this.accessTokenManager = accessTokenManager;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public JsApiTicket getTicket() {
        if (ticket != null && !isExpired()) {
            return ticket;
        }
        int retry = 0;
        do {
            String jsapiUrl = getJsApiUrl();
            Map body = restTemplate.getForObject(jsapiUrl, Map.class);
            int errcode = (int) body.getOrDefault("errcode", 0);
            String errmsg = (String) body.getOrDefault("errmsg", "");
            if (errcode != 0) {
                logger.warn(String.format("Get JsApi ticket failed: %s(%d)", errmsg, errcode));
                if (errcode == 40014) {
                    // invalid access token, refresh token
                    jsapiUrl = getJsApiUrl();
                }
            } else {
                try {
                    String ticket = body.get("ticket").toString();
                    int expiresIn = (int) body.get("expires_in");
                    expiresAt = Instant.now().getEpochSecond() + expiresIn;
                    this.ticket = new JsApiTicket(ticket, expiresIn);
                    logger.debug(String.format("Got JsApi ticket: %s, expires in %d seconds",
                            this.ticket.getTicket(), this.ticket.getExpiresIn()));
                    return this.ticket;
                } catch (Exception e) {
                    logger.warn("Parse JsApiTicket failed", e);
                }
            }
        } while(retry++ < 3);
        throw new WeChatException(-1, "Get JsApi ticket failed after 3 times retry");
    }

    private boolean isExpired() {
        return Instant.now().getEpochSecond() > expiresAt;
    }

    private String getJsApiUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(JSAPI_ENDPOINT);
        builder.queryParam("access_token", this.accessTokenManager.getAccessToken().getAccessToken());
        builder.queryParam("type", "jsapi");
        return builder.toUriString();
    }
}
