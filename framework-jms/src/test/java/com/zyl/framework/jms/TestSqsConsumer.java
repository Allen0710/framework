package com.zyl.framework.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zyl.framework.jms.sqs.AbstractSqsConsumer;

/**
 * simple sqs consumer data test
 */
public class TestSqsConsumer extends AbstractSqsConsumer<TestVO> {
    private static final Log logger = LogFactory.getLog(TestSqsConsumer.class);

    TestSqsConsumer(String queueName) {
        super(queueName);
    }

    @Override
    protected boolean internalProcess(TestVO testVO) {
        logger.info("TestSqsConsumer testVO :" + testVO.toString());
        return true;
    }
}
