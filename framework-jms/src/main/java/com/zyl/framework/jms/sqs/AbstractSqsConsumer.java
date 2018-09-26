package com.zyl.framework.jms.sqs;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.BatchResultErrorEntry;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteMessageBatchResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.zyl.framework.common.util.JsonUtil;
import com.zyl.framework.common.util.NullUtil;

/**
 * @author zhang
 */
public abstract class AbstractSqsConsumer<T> extends Thread {
    private static final Log logger = LogFactory.getLog(AbstractSqsConsumer.class);

    private static final String DEFAULT_REGION = "ap-southeast-1";

    private static final ClientConfiguration CLIENT_CONFIGURATION = new ClientConfiguration()
            .withMaxConnections(100);
    private static final AmazonSQS SQS_CLIENT = AmazonSQSClientBuilder.standard().withClientConfiguration
            (CLIENT_CONFIGURATION).withRegion(DEFAULT_REGION).build();

    private final Class<T> entityClass;
    private final AmazonSQS sqsClient;
    private final String queueUrl;
    private final Integer maxNumberOfMessages;
    private volatile AtomicBoolean stop = new AtomicBoolean(true);

    protected AbstractSqsConsumer(String queueName) {
        Assert.hasLength(queueName, "queueName can not be null");
        this.entityClass =
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.maxNumberOfMessages = 1;
        this.sqsClient = SQS_CLIENT;
        this.queueUrl = SQS_CLIENT.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
        Assert.hasLength(this.queueUrl, "queueUrl can not be null");
    }

    protected AbstractSqsConsumer(String queueName, Integer maxNumberOfMessages) {
        Assert.notNull(maxNumberOfMessages, "maxNumberOfMessages can not be null");
        Assert.state(maxNumberOfMessages > 0 && maxNumberOfMessages <= 50,
                "maxNumberOfMessages must be gt 0 and lt 50");
        Assert.hasLength(queueName, "queueName can not be null");
        this.entityClass =
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.maxNumberOfMessages = maxNumberOfMessages;
        this.sqsClient = SQS_CLIENT;
        this.queueUrl = SQS_CLIENT.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
        Assert.hasLength(this.queueUrl, "queueUrl can not be null");
    }

    protected AbstractSqsConsumer(AmazonSQS sqsClient, String queueName, Integer maxNumberOfMessages) {
        Assert.notNull(maxNumberOfMessages, "maxNumberOfMessages can not be null");
        Assert.state(maxNumberOfMessages > 0 && maxNumberOfMessages <= 50,
                "maxNumberOfMessages must be gt 0 and lt 50");
        Assert.notNull(sqsClient, "sqsClient can not be null");
        Assert.hasLength(queueName, "queueName can not be null");
        this.entityClass =
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.maxNumberOfMessages = maxNumberOfMessages;
        this.sqsClient = sqsClient;
        this.queueUrl = SQS_CLIENT.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
        Assert.hasLength(this.queueUrl, "queueUrl can not be null");
    }

    /**
     * pull message and execute
     */
    private void doStart() {
        try {
            while (!stop.get()) {
                // 构造批量删除batch request
                DeleteMessageBatchRequest deleteMessageBatchRequest =
                        new DeleteMessageBatchRequest().withQueueUrl(queueUrl);

                try {
                    final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
                    receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);
                    final ReceiveMessageResult result = sqsClient.receiveMessage(receiveMessageRequest);

                    if (!result.getMessages().isEmpty()) {

                        for (int i = 0; i < result.getMessages().size(); i++) {
                            try {
                                Message message = result.getMessages().get(i);
                                String body = message.getBody();
                                T t = JsonUtil.parseObject(body, entityClass);

                                // 具体业务consumer处理成功
                                if (internalProcess(t)) {
                                    deleteMessageBatchRequest
                                            .withEntries(new DeleteMessageBatchRequestEntry()
                                                    .withReceiptHandle(message.getReceiptHandle()));
                                } else {
                                    logger.warn("sqs consumer internal process fail; queueUrl =" + queueUrl + ";"
                                            + "messageId = " + message.getMessageId());
                                }

                            } catch (Exception e) {
                                logger.error("sqs consumer internal process fail; queueUrl =" + queueUrl, e);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("SqsConsumer exception : thread-name=" + this.getName() + ";queueUrl =" + queueUrl, e);
                } finally {
                    // start delete message batch
                    try {
                        if (NullUtil.isNotNullOrEmpty(deleteMessageBatchRequest.getEntries())) {
                            DeleteMessageBatchResult deleteMessageBatchResult = sqsClient.deleteMessageBatch
                                    (deleteMessageBatchRequest);
                            if (NullUtil.isNotNullOrEmpty(deleteMessageBatchResult.getFailed())) {
                                for (BatchResultErrorEntry resultErrorEntry : deleteMessageBatchResult.getFailed()) {
                                    logger.warn("sqs consumer delete message failed; id = " + resultErrorEntry
                                            .getId() + "message = " + resultErrorEntry.getMessage());
                                }
                            }
                        }
                    } catch (AmazonClientException e) {
                        logger.error("sqs consumer delete message batch exception", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Consumer exception: ", e);
        }
    }

    /**
     * 业务内部处理逻辑
     *
     * @param t
     *
     * @return
     */
    protected abstract boolean internalProcess(T t);

    @Override
    public void run() {
        doStart();
    }

    @Override
    public synchronized void start() {
        if (stop.compareAndSet(true, false)) {
            logger.info("sqs consumer start; thread name :" + this.getName());
            super.start();
        } else {
            logger.warn("sqs consumer has been start; thread name :" + this.getName());
        }
    }

    public void stopConsumer() {
        if (!stop.compareAndSet(false, true)) {
            logger.warn("sqs consumer has been stop; thread name :" + this.getName());
        }
    }
}
