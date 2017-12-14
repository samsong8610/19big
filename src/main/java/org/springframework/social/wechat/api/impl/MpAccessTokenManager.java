package org.springframework.social.wechat.api.impl;

import net.cmlzw.nineteen.controller.ConcurrentConflictException;
import org.springframework.social.wechat.api.MpAccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.wechat.api.MpAccessTokenRepository;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MpAccessTokenManager {
    private static final Logger logger = LoggerFactory.getLogger(MpAccessTokenManager.class);
    private final String appid;
    private final String appsecret;
    private MpAccessTokenRepository repository;
    private MpAccessToken accessToken;
    private RestTemplate internalRestTemplate;

    public MpAccessTokenManager(String appid, String appsecret, MpAccessTokenRepository repository) {
        this.appid = appid;
        this.appsecret = appsecret;
        this.repository = repository;
        this.internalRestTemplate = new RestTemplate();
    }

    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());
        restTemplate.setInterceptors(getInterceptors());
        restTemplate.setErrorHandler(getErrorHandler());
        return restTemplate;
    }

    public MpAccessToken getAccessToken() {
        refreshAccessToken();
        return accessToken;
    }

    private void refreshAccessToken() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = repository.get();
        }
        if (accessToken == null || accessToken.isExpired()) {
            int retry = 0;
            do {
                Map body = internalRestTemplate.getForObject(getAccessTokenUrl(), Map.class);
                if (body.containsKey("access_token")) {
                    long expiresIn = Long.parseLong(body.get("expires_in").toString());
                    if (accessToken == null) {
                        accessToken = new MpAccessToken(body.get("access_token").toString(), expiresIn);
                    } else {
                        accessToken.setAccessToken(body.get("access_token").toString());
                        accessToken.setExpiresIn(expiresIn);
                    }
                    break;
                } else {
                    logger.warn("get access token failed: %s(%s)",
                            body.getOrDefault("errmsg", ""), body.getOrDefault("errcode", ""));
                }
            } while (retry++ < 3);
            if (retry >= 3) {
                throw new RuntimeException("could not get access token after 3 times retry");
            }

            repository.save(accessToken);
        }
    }

    private String getAccessTokenUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/cgi-bin/token");
        builder.queryParam("grant_type", "client_credential")
                .queryParam("appid", this.appid)
                .queryParam("secret", this.appsecret);
        return builder.toUriString();
    }

    protected ResponseErrorHandler getErrorHandler() {
        // todo: refresh token when 401/403 by an error handler?
        return new DefaultResponseErrorHandler();
    }

    protected List<ClientHttpRequestInterceptor> getInterceptors() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AccessTokenParameterRequestInterceptor(accessToken.getAccessToken()));
        return interceptors;
    }

    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(getFormMessageConverter());
        messageConverters.add(getJsonMessageConverter());
        messageConverters.add(getByteArrayMessageConverter());
        return messageConverters;
    }

    protected HttpMessageConverter getFormMessageConverter() {
        FormHttpMessageConverter converter = new FormHttpMessageConverter();
        converter.setCharset(Charset.forName("UTF-8"));
        List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
        partConverters.add(new ByteArrayHttpMessageConverter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        partConverters.add(stringHttpMessageConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        converter.setPartConverters(partConverters);
        return converter;
    }

    protected HttpMessageConverter getJsonMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = converter.getSupportedMediaTypes();
        List<MediaType> newMediaTypes = new ArrayList<>(supportedMediaTypes.size() + 1);
        newMediaTypes.add(MediaType.TEXT_PLAIN);
        converter.setSupportedMediaTypes(newMediaTypes);
        return converter;
    }

    protected HttpMessageConverter getByteArrayMessageConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG));
        return converter;
    }
}
