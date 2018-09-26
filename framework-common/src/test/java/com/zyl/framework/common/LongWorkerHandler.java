package com.zyl.framework.common;

import com.zyl.framework.common.disruptor.AbstractWorkHandler;

public class LongWorkerHandler extends AbstractWorkHandler<LongEvent> {
    @Override
    protected void execute(LongEvent event) {
//        try {
//            Thread.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //        System.out.println(event.toString());
    }
}
