package org.springframework.social.wechat.api.impl.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JsApiTicketMixin {
    @JsonProperty("ticket")
    private String ticket;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("errcode")
    private int errcode;
    @JsonProperty("errmsg")
    private String errmsg;
}
