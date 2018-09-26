package com.zyl.framework.common.test;

import com.lmax.disruptor.EventFactory;

public class ValueEvent {
    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    public static final EventFactory<ValueEvent> EVENT_FACTORY = () -> new ValueEvent();
}
