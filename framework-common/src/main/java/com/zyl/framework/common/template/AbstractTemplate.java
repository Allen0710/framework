package com.zyl.framework.common.template;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import com.zyl.framework.common.BaseRequest;

/**
 * 异步调用抽象类
 * 这里仅仅提供少量的调取方法，可以自行扩展
 */
public abstract class AbstractTemplate implements Template {
    public AsyncRestTemplate asyncRestTemplate;

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest, Class<T> responseType)
            throws Exception {
        String url = baseRequest.getUrl();
        try {
            ListenableFuture<ResponseEntity<T>> t = asyncRestTemplate.getForEntity(url, responseType);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest,
                                                                     ParameterizedTypeReference<T> responseType) throws Exception {
        String url = baseRequest.getUrl();
        try {
            ListenableFuture<ResponseEntity<T>> t = asyncRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest, Class<T> responseType,
                                                                     Map<String, ?> uriVariables) throws Exception {
        String url = baseRequest.getUrl();
        ListenableFuture<ResponseEntity<T>> t = null;
        try {
            t = asyncRestTemplate.exchange(url, HttpMethod.GET, null, responseType, uriVariables);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest,
                                                                     ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws Exception {
        String url = baseRequest.getUrl();
        ListenableFuture<ResponseEntity<T>> t = null;
        try {
            t = asyncRestTemplate.exchange(url, HttpMethod.GET, null, responseType, uriVariables);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest, Map<String,?> entities,
                                                                      Class<T> responseType)
            throws Exception {
        String url = baseRequest.getUrl();
        try {
            HttpEntity<?> entity = new HttpEntity<>(entities);
            ListenableFuture<ResponseEntity<T>> t = asyncRestTemplate.postForEntity(url, entity, responseType);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest, Map<String,?> entities,
                                                                     ParameterizedTypeReference<T> responseType) throws Exception {
        String url = baseRequest.getUrl();
        try {
            HttpEntity<?> entity = new HttpEntity<>(entities);
            ListenableFuture<ResponseEntity<T>> t = asyncRestTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest, Map<String,?> entities,Class<T> responseType,
                                                                     Map<String, ?> uriVariables) throws Exception {
        String url = baseRequest.getUrl();
        ListenableFuture<ResponseEntity<T>> t = null;
        try {
            HttpEntity<?> entity = new HttpEntity<>(entities);
            t = asyncRestTemplate.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest,Map<String,?> entities,
                                                                     ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws Exception {
        String url = baseRequest.getUrl();
        ListenableFuture<ResponseEntity<T>> t = null;
        try {
            HttpEntity<?> entity = new HttpEntity<>(entities);
            t = asyncRestTemplate.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    abstract void setTemplate(AsyncRestTemplate asyncRestTemplate);

}
