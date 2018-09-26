package com.zyl.framework.common.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * @author zhang
 */
public class CommonEventProducer<T> {
    private final RingBuffer<CommonEvent<T>> ringBuffer;

    public CommonEventProducer(RingBuffer<CommonEvent<T>> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }

    public void publish(T t)
    {
        // Grab the next sequence
        long sequence = ringBuffer.next();
        try {
            CommonEvent event = ringBuffer.get(sequence);
            event.setData(t);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public void publish(T[] ts) {
        int length = ts.length;
        long hi = ringBuffer.next(length);
        long lo = hi - (length - 1);
        try {
            for (long l = lo; l <= hi; l++) {
                CommonEvent event = ringBuffer.get(l);
                event.setData(ts[(int)(l - lo)]);
            }
        } finally {
            ringBuffer.publish(lo, hi);
        }
    }

    public void publishEvent(CommonEventTranslator<T> translator,T t) {
        ringBuffer.publishEvent(translator, t);
    }
}
