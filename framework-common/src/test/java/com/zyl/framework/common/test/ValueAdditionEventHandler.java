package com.zyl.framework.common.test;

import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.EventHandler;

public class ValueAdditionEventHandler implements EventHandler<ValueEvent>
{
    private long value = 0;
    private long count;
    private CountDownLatch latch;
    public long getValue()
    {
        return value;
    }
    public void reset(final CountDownLatch latch, final long expectedCount)
    {
        value = 0;
        this.latch = latch;
        count = expectedCount;
    }
    @Override
    public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception
    {
        value = event.getValue();
        if (count == sequence)
        {
            latch.countDown();
        }
    }
}