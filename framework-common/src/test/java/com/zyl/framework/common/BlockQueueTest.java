package com.zyl.framework.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zyl.framework.common.disruptor.CommonEvent;

public class BlockQueueTest {

    public static void main(String[] args) throws Exception{
        Long produceNum = 1000000L;
        CountDownLatch downLatch = new CountDownLatch(produceNum.intValue());
        CountDownLatch produceLatch = new CountDownLatch(produceNum.intValue());

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024 * 64;

        // Construct the blockingQueue
        ArrayBlockingQueue<CommonEvent<LongEvent>> blockingQueue = new ArrayBlockingQueue<>(bufferSize);

        Integer consumeNum = 1;
        Executor executor = Executors.newFixedThreadPool(consumeNum);
        for (int i = 0; i < consumeNum; i++) {
            ((ExecutorService) executor).submit(() -> {
                while (true) {
                    CommonEvent event = blockingQueue.take();
//                    Thread.sleep(20);
                    downLatch.countDown();
//                    System.out.println("LongEvent consumer1: " + event.toString());
//                    try {
//                        Thread.sleep(80);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            });
        }
        Executor producePool = Executors.newFixedThreadPool(1);
        Long start = System.currentTimeMillis();
        for (int l = 0; l < produceNum; l++)
        {
            CommonEvent<LongEvent> commonEvent = new CommonEvent<>();
            LongEvent longEvent = new LongEvent();
            longEvent.setValue(l);
            commonEvent.setData(longEvent);
            try {
                blockingQueue.put(commonEvent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                produceLatch.countDown();
            }
        }
        produceLatch.await();
        System.out.println("produce all time = " + (System.currentTimeMillis() - start));
        System.out.println("produce all qps = " + produceNum * 1000 / (System.currentTimeMillis() - start));
        downLatch.await();
        System.out.println("consume all time = " + (System.currentTimeMillis() - start));
        System.out.println("consume all qps = " + produceNum * 1000 / (System.currentTimeMillis() - start));
        ((ExecutorService) executor).shutdownNow();
        ((ExecutorService) producePool).shutdown();
    }
}
