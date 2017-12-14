package org.springframework.social.wechat.api.impl.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.social.wechat.api.JsApiTicket;
import org.springframework.social.wechat.api.WeChatUserProfile;

public class WeChatModule extends SimpleModule {
    public WeChatModule() {
        super("WeChatModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(WeChatUserProfile.class, WeChatUserProfileMixin.class);
    }
}
