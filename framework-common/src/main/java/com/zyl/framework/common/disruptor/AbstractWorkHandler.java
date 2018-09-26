package com.zyl.framework.common.disruptor;

import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.WorkHandler;

/**
 * @author zhang
 */
public abstract class AbstractWorkHandler<T> implements WorkHandler<CommonEvent<T>> {
    private CountDownLatch downLatch;

    @Override
    public void onEvent(CommonEvent<T> event) throws Exception {
        execute(event.getData());
        if (null != downLatch) {
            downLatch.countDown();
        }
    }

    public void reset(CountDownLatch downLatch) {
        this.downLatch = downLatch;
    }

    protected abstract void execute(T event);
}
