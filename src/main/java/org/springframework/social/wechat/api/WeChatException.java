package org.springframework.social.wechat.api;

public class WeChatException extends RuntimeException {
    private int errcode;
    private String errmsg;

    public WeChatException(int errcode, String errmsg) {
        super(String.format("%s(errcode: %d)", errmsg, errcode));
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
