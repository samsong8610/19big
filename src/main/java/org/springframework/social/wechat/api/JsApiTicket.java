package org.springframework.social.wechat.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsApiTicket {
    @JsonProperty("ticket")
    private String ticket;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("errcode")
    private int errcode;
    @JsonProperty("errmsg")
    private String errmsg;

    public JsApiTicket() {}
    public JsApiTicket(String ticket, int expiresIn) {
        this.ticket = ticket;
        this.expiresIn = expiresIn;
    }
    public JsApiTicket(int errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
