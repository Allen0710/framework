package com.zyl.framework.common.disruptor;

import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.EventHandler;

/**
 * @author zhang
 */
public abstract class AbstractEventHandler<T> implements EventHandler<CommonEvent<T>> {
    private CountDownLatch downLatch;
    @Override
    public void onEvent(CommonEvent<T> event, long sequence, boolean endOfBatch) throws Exception {
        execute(event.getData());
        if (null != downLatch) {
            downLatch.countDown();
        }
    }

    public void reset(final CountDownLatch downLatch) {
        this.downLatch = downLatch;
    }

    protected abstract void execute(T data);
}
