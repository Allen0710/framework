package com.zyl.framework.common.disruptor;

/**
 * @author zhang
 */
public class CommonEvent<T> {
    private T data;

    private Long timeStap;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimeStap() {
        return timeStap;
    }

    public void setTimeStap(Long timeStap) {
        this.timeStap = timeStap;
    }
}
