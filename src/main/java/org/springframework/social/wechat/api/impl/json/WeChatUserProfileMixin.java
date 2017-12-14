package org.springframework.social.wechat.api.impl.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.social.wechat.api.Gender;

import java.util.Set;

abstract class WeChatUserProfileMixin {
    @JsonProperty("sex")
    Gender sex;
    @JsonProperty("country")
    String country;
    @JsonProperty("province")
    String province;
    @JsonProperty("city")
    String city;
    @JsonProperty("language")
    String language;
    @JsonProperty("privilege")
    Set<String> privileges;

    public WeChatUserProfileMixin(
            @JsonProperty("unionid") String unionId,
            @JsonProperty("openid") String openId,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("headimgurl") String headImgUrl) {}
}
