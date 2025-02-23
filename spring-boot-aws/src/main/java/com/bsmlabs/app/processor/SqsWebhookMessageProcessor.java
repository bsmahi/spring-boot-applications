package com.bsmlabs.app.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class SqsWebhookMessageProcessor {

    private final SqsAsyncClient sqsAsyncClient;
    private final SqsClient sqsClient;
    private final String queueUrl;
    private final String asyncQueueUrl;

    public SqsWebhookMessageProcessor(SqsAsyncClient sqsAsyncClient,
                                      SqsClient sqsClient,
                                      @Value("${aws.sqs.queue.url}") String queueUrl,
                                      @Value("${aws.sqs.queue.asyncUrl}") String asyncQueueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.asyncQueueUrl = asyncQueueUrl;
    }

    @Scheduled(fixedDelay = 10000) // Polls every 10 seconds
    public void pollAsyncMessages() throws ExecutionException, InterruptedException {
        var receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(asyncQueueUrl)
                .maxNumberOfMessages(5) // Fetch up to 5 messages at a time
                .waitTimeSeconds(10) // Long polling
                .build();

        CompletableFuture<ReceiveMessageResponse> messageResponse = sqsAsyncClient.receiveMessage(receiveRequest);

        messageResponse.thenAccept(response -> {
            response.messages().forEach(message -> {
                log.info("Async Received message: {}", message.body());
                // Process the webhook message
                processMessage(message.body());

                // Delete Async message after processing
                deleteAsyncMessage(message.receiptHandle());
            });
        });
    }

    @Scheduled(fixedDelay = 10000) // Polls every 10 seconds
    public void pollMessages() {
        var receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5) // Fetch up to 5 messages at a time
                .waitTimeSeconds(10) // Long polling
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        messages.forEach(message -> {
            log.info("Received message: {}", message.body());
            // Process the webhook message
            processMessage(message.body());

            // Delete message after processing
            deleteMessage(message.receiptHandle());
        });
    }

    private void processMessage(String messageBody) {
        // Add custom webhook message processing logic here
        log.info("Processing webhook message: {}", messageBody);
    }

    private void deleteAsyncMessage(String receiptHandle) {
        var deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(asyncQueueUrl)
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(deleteRequest);
        log.info("Async Message deleted from queue");
    }

    private void deleteMessage(String receiptHandle) {
        var deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(deleteRequest);
        log.info("Message deleted from queue");
    }
}
