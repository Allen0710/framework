package com.zyl.framework.common;

import org.springframework.http.HttpMethod;

/**
 *
 */
public class BaseRequest {
    protected String url;

    private HttpMethod method;

    public BaseRequest(String url) {
        this.url = url;
        this.method = HttpMethod.GET;
    }

    public BaseRequest(String url,HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
