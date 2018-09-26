package com.zyl.framework.jms.sqs;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.BatchResultErrorEntry;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteMessageBatchResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.zyl.framework.common.util.JsonUtil;
import com.zyl.framework.common.util.NullUtil;

/**
 * @author zhang
 */
public class SqsProducer<T> {
    private static final Log logger = LogFactory.getLog(SqsProducer.class);

    private static final String DEFAULT_REGION = "ap-southeast-1";

    private static final ClientConfiguration CLIENT_CONFIGURATION = new ClientConfiguration()
            .withMaxConnections(100);
    private static final AmazonSQS SQS_CLIENT = AmazonSQSClientBuilder.standard().withClientConfiguration
            (CLIENT_CONFIGURATION).withRegion(DEFAULT_REGION).build();

    private final AmazonSQS sqsClient;
    private final String queueUrl;

    public SqsProducer(String queueName) {
        Assert.hasLength(queueName, "queueName can not be null");
        this.sqsClient = SQS_CLIENT;
        this.queueUrl = SQS_CLIENT.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
    }

    public SqsProducer(AmazonSQS sqsClient, String queueName) {
        Assert.notNull(sqsClient, "sqsClient can not be null");
        Assert.hasLength(queueName, "queueName can not be null");
        this.sqsClient = sqsClient;
        this.queueUrl = SQS_CLIENT.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
        Assert.hasLength(this.queueUrl, "queueUrl can not be null");
    }

    /**
     * sqs发送数据
     *
     * @param data
     */
    public boolean sendMessage(T data) {
        // Json 序列化传输，为了减少网络传输数据，过滤掉为null的字段
        String message = JsonUtil.toJsonString(data, true);
        SendMessageResult sendMessageResult = sqsClient.sendMessage(new SendMessageRequest(queueUrl,
                message));
        if (null == sendMessageResult) {
            logger.error("SqsProducer send message result is null; queueUrl = " + queueUrl + "; sqsClient = "
                    + sqsClient);
            return false;
        }
        return true;
    }

    /**
     * sqs批量发送数据，如返回包含失败，则尝试回滚成功的message，一定程度保证原子性
     * @param dataList
     */
    public boolean sendMessageBatch(List<T> dataList) {
        Assert.notEmpty(dataList, "produce data list can not be null");
        SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest().withQueueUrl(queueUrl);
        dataList.forEach(data -> {
            // Json 序列化传输，为了减少网络传输数据，过滤掉为null的字段
            sendMessageBatchRequest.withEntries(new SendMessageBatchRequestEntry()
                    .withMessageBody(JsonUtil.toJsonString(data, true)));
        });

        // send message batch
        SendMessageBatchResult sendMessageResult = sqsClient.sendMessageBatch(sendMessageBatchRequest);

        if (NullUtil.isNotNullOrEmpty(sendMessageResult.getFailed())) {
            logger.error("SqsProducer send message batch fail, try cancel success message, queueUrl = " + queueUrl +
                    "; " + "sqsClient = " + sqsClient);
            sendMessageResult.getFailed().forEach(fail -> logger
                    .warn("send message batch failed ; id = " + fail.getId() + "message = " + fail.getMessage()));

            if (NullUtil.isNotNullOrEmpty(sendMessageResult.getSuccessful())) {
                // 批量插入失败，尝试删除成功的部分，保持批量插入的原子性
                DeleteMessageBatchRequest deleteMessageBatchRequest =
                        new DeleteMessageBatchRequest().withQueueUrl(queueUrl);
                sendMessageResult.getSuccessful().forEach(success -> deleteMessageBatchRequest.withEntries(new
                        DeleteMessageBatchRequestEntry().withId
                        (success.getId())));

                // try delete message submit
                DeleteMessageBatchResult deleteMessageBatchResult = sqsClient.deleteMessageBatch
                        (deleteMessageBatchRequest);

                if (NullUtil.isNotNullOrEmpty(deleteMessageBatchResult.getFailed())) {
                    for (BatchResultErrorEntry resultErrorEntry : deleteMessageBatchResult.getFailed()) {
                        logger.warn("send message batch failed and try delete message failed; id = " +
                                resultErrorEntry.getId() + "message = " + resultErrorEntry.getMessage());
                    }
                }
            }
            return false;
        }
        return true;
    }
}
