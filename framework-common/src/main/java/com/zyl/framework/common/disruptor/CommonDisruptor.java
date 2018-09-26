package com.zyl.framework.common.disruptor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @author zhang
 */
public class CommonDisruptor<T> extends Disruptor<CommonEvent<T>> {
    private static final int DEFAULT_THREAD_NUM = 10;
    private static final int DEFAULT_RING_BUFFER_NUM = 1024;
    private static final int MAX_THREAD_NUM = 50;
    private static AtomicInteger NUM = new AtomicInteger(1);

    /**
     * Executor that will be used to construct new threads for consumers
      */
    private ExecutorService executor;

    private static ExecutorService EXECUTOR = new ThreadPoolExecutor(DEFAULT_THREAD_NUM, MAX_THREAD_NUM, 30000, TimeUnit
            .MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
                SecurityManager s = System.getSecurityManager();
                ThreadGroup group = (s != null) ? s.getThreadGroup() :
                        Thread.currentThread().getThreadGroup();
                return new Thread(group, r,"common_disruptor_" + NUM
                                .getAndIncrement(),0);
            });


    public CommonDisruptor(EventFactory<CommonEvent<T>> eventFactory) {
        super(eventFactory,DEFAULT_RING_BUFFER_NUM, EXECUTOR);
        this.executor = EXECUTOR;
    }

    public CommonDisruptor(EventFactory<CommonEvent<T>> eventFactory, int ringBufferSize) {
        super(eventFactory,ringBufferSize, EXECUTOR);
        this.executor = EXECUTOR;
    }

    public CommonDisruptor(EventFactory<CommonEvent<T>> eventFactory, int ringBufferSize, Executor executor) {
        super(eventFactory,ringBufferSize, executor);
        this.executor = (ExecutorService) executor;
    }

    public CommonDisruptor(EventFactory<CommonEvent<T>> eventFactory, int ringBufferSize, Executor executor,
                           ProducerType producerType, WaitStrategy waitStrategy) {
        super(eventFactory,ringBufferSize, executor,producerType,waitStrategy);
        this.executor = (ExecutorService) executor;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        executor.shutdown();
    }
}
