package com.zyl.framework.common;

import com.zyl.framework.common.disruptor.AbstractEventHandler;

public class LongEventHandler extends AbstractEventHandler<LongEvent> {
    @Override
    protected void execute(LongEvent event) {
//        System.out.println(event.toString());
    }
}
