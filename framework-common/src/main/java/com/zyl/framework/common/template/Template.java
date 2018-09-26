package com.zyl.framework.common.template;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import com.zyl.framework.common.BaseRequest;

/**
 * 异步调用模板接口
 */
public interface Template {
    <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest, Class<T> responseType) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest, ParameterizedTypeReference<T> responseType) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest, Class<T> responseType, Map<String, ?> uriVariables) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> getAsyncForObject(BaseRequest baseRequest, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest, Map<String,?> entities,
                                                               Class<T>responseType) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest,Map<String,?> entities,ParameterizedTypeReference<T> responseType) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest,Map<String,?> entities, Class<T> responseType, Map<String, ?> uriVariables) throws Exception;

    <T> ListenableFuture<ResponseEntity<T>> postAsyncForObject(BaseRequest baseRequest,Map<String,?> entities, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws Exception;
}
