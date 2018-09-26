package com.zyl.framework.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

/**
 *异步client Demo测试
 */
public class ClientDemo {
    public static void main(String[] args) {
        BatchRequestWrapper wrapper = new BatchRequestWrapper();
        Map<BaseRequest, Class<? extends BaseResponse>> getRequest = new HashMap<>();
        Map<BaseRequest, Class<? extends BaseResponse>> postRequest = new HashMap<>();
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> entities = new HashMap<>();
        variables.put("item_id", 82);
        entities.put("partner", "123");
        entities.put("reason", "345");
        getRequest.put(new BaseRequest("http://localhost:8080/v2/video/detail"), BaseResponse.class);
        postRequest.put(new BaseRequest("http://localhost:8080/v2/partner/token/create", HttpMethod.POST), BaseResponse.class);
        wrapper.setParams(TestEnum.VIDEODETAIL,variables, getRequest);
        wrapper.setParams(TestEnum.TOKENCREATE,entities,variables, postRequest);

        FutureAsyncHttpClient asyncHttpClient = new FutureAsyncHttpClient();
        Map<BaseEnum, Object> futureData = asyncHttpClient.doExecute(wrapper);
        for (BaseEnum baseEnum : futureData.keySet()) {
            System.err.println(baseEnum + "=" + futureData.get(baseEnum).toString());
        }
    }
}
