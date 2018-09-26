package com.zyl.framework.common.disruptor;

import com.lmax.disruptor.EventFactory;
import com.zyl.framework.common.util.ReflectionUtils;

/**
 * 事件工厂
 * @author zhang
 */
public class CommonEventFactory<T> implements EventFactory<CommonEvent<T>> {
    @Override
    public CommonEvent<T> newInstance() {
        return new CommonEvent<>();
    }
}
