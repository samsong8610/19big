package org.springframework.social.wechat.api.impl.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.social.wechat.api.JsApiTicket;

public class WeChatMpModule extends SimpleModule {
    public WeChatMpModule() {
        super("WeChatMpModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(JsApiTicket.class, JsApiTicketMixin.class);
    }
}
