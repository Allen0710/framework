package com.zyl.framework.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.zyl.framework.common.disruptor.CommonEventFactory;
import com.zyl.framework.common.disruptor.CommonDisruptor;
import com.zyl.framework.common.disruptor.CommonEvent;
import com.zyl.framework.common.disruptor.CommonEventProducer;
import com.zyl.framework.common.disruptor.CommonEventTranslator;

public class DisruptorTest {

    public static void main(String[] args) throws Exception{
        Long produceNum = 1000000L;
        CountDownLatch downLatch = new CountDownLatch(produceNum.intValue());
        CountDownLatch produceLatch = new CountDownLatch(produceNum.intValue());

        // The factory for the event
        CommonEventFactory<LongEvent> factory = new CommonEventFactory<>();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024 * 64;

        // Construct the Disruptor
        Disruptor<CommonEvent<LongEvent>> disruptor = new CommonDisruptor<>(factory, bufferSize,Executors
                .newCachedThreadPool(),ProducerType.SINGLE,new BusySpinWaitStrategy());

        // Connect the handler
//        LongEventHandler eventHandler = new LongEventHandler();
//        eventHandler.reset(downLatch);
//        disruptor.handleEventsWith(eventHandler);
//        }, new AbstractEventHandler<LongEvent>() {
//            @Override
//            protected void execute(LongEvent event) {
//                System.out.println("LongEvent consumer2: " + event.toString());
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).then(new AbstractEventHandler<LongEvent>() {
//            @Override
//            protected void execute(LongEvent event) {
//                downLatch.countDown();
//                System.out.println("LongEvent consumer3: " + event.toString());
//                try {
//                    Thread.sleep(30);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        Integer consumeNum = 1;
        WorkHandler[] workHandlers = new WorkHandler[consumeNum];
        for (int i = 0; i < consumeNum; i++) {
            LongWorkerHandler workerHandler = new LongWorkerHandler();
            workerHandler.reset(downLatch);
            workHandlers[i] = workerHandler;
        }
//        WorkHandler workHandler = new AbstractWorkHandler<LongEvent>() {
//            @Override
//            protected void execute(LongEvent event) {
//                System.out.println("LongEvent consumer1: " + event.toString());
//                try {
//                    Thread.sleep(80);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
        disruptor.handleEventsWithWorkerPool(workHandlers);

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CommonEvent<LongEvent>> ringBuffer = disruptor.getRingBuffer();

        CommonEventProducer<LongEvent> eventProducer = new CommonEventProducer<>(ringBuffer);
        CommonEventTranslator<LongEvent> commonEventTranslator = new CommonEventTranslator<>();

        ExecutorService producePool = Executors.newFixedThreadPool(1);
        Long start = System.currentTimeMillis();
        for (long l = 0; l < produceNum; l++)
        {
            LongEvent longEvent = new LongEvent();
            longEvent.setValue(l);
            eventProducer.publishEvent(commonEventTranslator,longEvent);
            produceLatch.countDown();
        }
        produceLatch.await();
        System.out.println("produce all time = " + (System.currentTimeMillis() - start));
        System.out.println("produce all qps = " + (produceNum * 1000) / (System.currentTimeMillis() - start));
        downLatch.await();
        System.out.println("consume all time = " + (System.currentTimeMillis() - start));
        System.out.println("consume all qps = " + (produceNum * 1000) / (System.currentTimeMillis() - start));
        disruptor.shutdown();
        producePool.shutdown();
    }
}
