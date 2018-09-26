package com.zyl.framework.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import com.zyl.framework.common.template.CommonListenableCallBack;
import com.zyl.framework.common.template.SimpleAsyncClientTemplate;

/**
 *FutureAsyncHttpClient的构造函数可以传入自定义的asyncClientTemplate，不传的话就是默认的
 * 其中的executeHttp()方法传入多个请求的包装类BatchRequestWrapper，返回数据组成的Map
 * 其中Map中的key对应的是业务标识，value对应的是请求对应的结果类
 */
public class FutureAsyncHttpClient {
    public SimpleAsyncClientTemplate asyncClientTemplate;

    public FutureAsyncHttpClient(){
        asyncClientTemplate = new SimpleAsyncClientTemplate(null);
    }

    public FutureAsyncHttpClient(AsyncRestTemplate tp) {
        asyncClientTemplate = new SimpleAsyncClientTemplate(tp);
    }

    //获取数据
    public Map<BaseEnum, Object> doExecute(BatchRequestWrapper wrapper) {
        if (wrapper == null)
            return new HashMap<>();
        final CountDownLatch latch = new CountDownLatch(wrapper.getWrapper().size());
        final Map<BaseEnum, Object> result = new HashMap<>();

        if (wrapper.getWrapper() != null) {
            for (final BatchRequestWrapper.RequestWrapper wp : wrapper.getWrapper()) {
                try {
                    Map<BaseRequest, ?> requestMap = wp.getRequest();
                    for (final BaseRequest tpRequestInfo : requestMap.keySet()) {
                        doExecute(wp, tpRequestInfo, latch, requestMap, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                latch.await();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return result;
    }

    //发送http请求，获取请求结果
    private void doExecute(BatchRequestWrapper.RequestWrapper wp, BaseRequest tpRequestInfo, CountDownLatch latch,
                           Map<BaseRequest, ?> requestMap, Map<BaseEnum, Object> result) throws Exception {
        ListenableFuture<?> statResponse = null;
        switch (tpRequestInfo.getMethod()) {
            case GET:
                if (requestMap.get(tpRequestInfo) instanceof ParameterizedTypeReference<?>) {
                    ParameterizedTypeReference<?> responseType =
                            (ParameterizedTypeReference<?>) requestMap.get(tpRequestInfo);
                    statResponse = asyncClientTemplate.getAsyncForObject(tpRequestInfo, responseType, wp.getVariables());
                } else if (requestMap.get(tpRequestInfo) instanceof Class<?>) {
                    Class<?> responseType = (Class<?>) requestMap.get(tpRequestInfo);
                    statResponse = asyncClientTemplate.getAsyncForObject(tpRequestInfo, responseType, wp.getVariables());
                }else {
                    throw new RuntimeException("requestType error...");
                }
                break;
            case POST:
                if (requestMap.get(tpRequestInfo) instanceof ParameterizedTypeReference<?>) {
                    ParameterizedTypeReference<?> responseType =
                            (ParameterizedTypeReference<?>) requestMap.get(tpRequestInfo);
                    statResponse = asyncClientTemplate.postAsyncForObject(tpRequestInfo, wp.getEntities(),
                            responseType, wp.getVariables());
                } else if (requestMap.get(tpRequestInfo) instanceof Class<?>) {
                    Class<?> responseType = (Class<?>) requestMap.get(tpRequestInfo);
                    statResponse = asyncClientTemplate.postAsyncForObject(tpRequestInfo, wp.getEntities(),
                            responseType, wp.getVariables());
                }else {
                    throw new RuntimeException("requestType error...");
                }
                break;
            default:
                throw new RuntimeException("can not support http method:" + tpRequestInfo.getMethod().name());
        }
        addCallBack(statResponse, wp.getBaseEnum(), latch, result);
    }

    //增加回调
    private <T> void addCallBack(ListenableFuture<T> statResponse, BaseEnum baseEnum, CountDownLatch latch,
                                 Map<BaseEnum, Object> result) {
        if (statResponse != null) {
            statResponse.addCallback(new CommonListenableCallBack<T>(baseEnum, result, latch));
        }
    }
}
