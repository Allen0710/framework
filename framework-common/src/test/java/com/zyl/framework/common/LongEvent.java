package com.zyl.framework.common;

public class LongEvent {
    private long value;

    @Override
    public String toString() {
        return "[value=" + value +"]";
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
