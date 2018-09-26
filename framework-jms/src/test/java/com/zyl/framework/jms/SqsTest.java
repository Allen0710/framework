package com.zyl.framework.jms;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zyl.framework.jms.sqs.SqsProducer;

public class SqsTest {
    private static final String DEFAULT_QUEUE_NAME = "zyl_analytics_dev";

    private SqsProducer<TestVO> sqsProducer;

    private TestSqsConsumer simpleSqsConsumer;

    @Before
    public void init() {
        simpleSqsConsumer = new TestSqsConsumer(DEFAULT_QUEUE_NAME);
        simpleSqsConsumer.start();
        sqsProducer = new SqsProducer<>(DEFAULT_QUEUE_NAME);
    }

    @After
    public void destroy() {
        simpleSqsConsumer.stopConsumer();
    }

    @Test
    public void test() {
        TestVO testVO = new TestVO();
        testVO.setUserId("zhangyl");
        testVO.setEmail("zhangyalun@qq.com");
        boolean result = sqsProducer.sendMessage(testVO);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }
}
