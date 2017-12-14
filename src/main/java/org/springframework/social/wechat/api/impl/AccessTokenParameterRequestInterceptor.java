package org.springframework.social.wechat.api.impl;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.support.HttpRequestDecorator;

import java.io.IOException;

public class AccessTokenParameterRequestInterceptor implements ClientHttpRequestInterceptor {
    private String accessToken;
    private String parameterName;

    public AccessTokenParameterRequestInterceptor(String accessToken) {
        this(accessToken, "access_token");
    }

    public AccessTokenParameterRequestInterceptor(String accessToken, String parameterName) {
        this.accessToken = accessToken;
        this.parameterName = parameterName;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestDecorator httpRequest = new HttpRequestDecorator(request);
        httpRequest.addParameter(parameterName, accessToken);
        return execution.execute(httpRequest, body);
    }
}
