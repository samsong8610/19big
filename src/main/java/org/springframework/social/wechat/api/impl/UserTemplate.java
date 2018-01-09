package org.springframework.social.wechat.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.wechat.api.UserOperations;
import org.springframework.social.wechat.api.WeChatException;
import org.springframework.social.wechat.api.WeChatUserProfile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

public class UserTemplate implements UserOperations {
    private static final Logger logger = LoggerFactory.getLogger(UserTemplate.class);
    private RestTemplate restTemplate;
    private final boolean isAuthorized;
    private String openId;

    public UserTemplate(RestTemplate restTemplate, boolean authorized) {
        this.restTemplate = restTemplate;
        this.isAuthorized = authorized;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String getProfileId() {
        return getUserProfile().getOpenId();
    }

    @Override
    public WeChatUserProfile getUserProfile() {
        // TODO: error response parsing
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(snsUserInfoUrl());
        if (openId != null) {
            builder.queryParam("openid", this.openId);
        }
        // TODO: remove these debug lines
        String resp = restTemplate.getForObject(builder.build().toUri(), String.class);
        if (resp != null && resp.contains("errcode")) {
            logger.warn("getUserProfile return error: " + resp);
        }
        WeChatUserProfile profile = restTemplate.getForObject(builder.build().toUri(), WeChatUserProfile.class);
        return profile;
    }

    private String snsUserInfoUrl() {
        return "https://api.weixin.qq.com/sns/userinfo";
    }
}
