package org.springframework.social.wechat.api.impl;

import org.springframework.social.wechat.api.JsApiOperations;
import org.springframework.social.wechat.api.WeChatMp;

public class WeChatMpTemplate implements WeChatMp {
    private MpAccessTokenManager tokenManager;
    private JsApiTemplate jsApiTemplate;

    public WeChatMpTemplate(MpAccessTokenManager tokenManager) {
        this.tokenManager = tokenManager;
        jsApiTemplate = new JsApiTemplate(tokenManager);
    }

    @Override
    public JsApiOperations jsApiOperations() {
        return jsApiTemplate;
    }
}
