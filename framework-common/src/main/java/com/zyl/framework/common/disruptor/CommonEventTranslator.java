package com.zyl.framework.common.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * @author zhang
 */
public class CommonEventTranslator<T> implements EventTranslatorOneArg<CommonEvent<T>,T> {
    @Override
    public void translateTo(CommonEvent<T> commonEvent, long l, T t) {
        translate(commonEvent, t);
    }

    protected void translate(CommonEvent<T> commonEvent, T t) {
        commonEvent.setData(t);
    }
}
