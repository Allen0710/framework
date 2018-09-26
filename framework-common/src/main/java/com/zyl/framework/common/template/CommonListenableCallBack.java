package com.zyl.framework.common.template;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.zyl.framework.common.BaseEnum;

/**
 * 实现ListenableFutureCallback，实现回调功能
 * @param <T>
 */
public class CommonListenableCallBack<T> implements ListenableFutureCallback<T> {
    private BaseEnum type;
    private Map<BaseEnum, Object> resultValue;
    private volatile CountDownLatch latch;

    public CommonListenableCallBack(BaseEnum type, Map<BaseEnum, Object> resultValue, CountDownLatch latch) {
        this.type = type;
        this.resultValue = resultValue;
        this.latch = latch;
    }

    @Override
    public void onSuccess(T result) {
        ResponseEntity<T> re = (ResponseEntity<T>) result;
        if (re != null && re.getBody() != null) {
            T body = re.getBody();
            if (type != null) {
                resultValue.put(type, body);
            }
        }
        latch.countDown();
    }

    @Override
    public void onFailure(Throwable ex) {
        System.out.println(ex);
        latch.countDown();
    }

}
