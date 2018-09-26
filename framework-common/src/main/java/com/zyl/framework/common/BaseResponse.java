package com.zyl.framework.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 基础响应抽象类，提供状态码
 */
public class BaseResponse<T> implements Serializable {
    @JsonProperty("result_code")
    private int code;

    private String message;

    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
