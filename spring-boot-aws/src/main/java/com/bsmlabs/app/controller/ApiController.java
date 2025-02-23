package com.bsmlabs.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SqsAsyncClient sqsAsyncClient;
    private final String asyncQueueUrl;


    public ApiController(SqsAsyncClient sqsAsyncClient,
                         @Value("${aws.sqs.queue.asyncUrl}") String asyncQueueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.asyncQueueUrl = asyncQueueUrl;
    }

    @PostMapping("/send")
    public ResponseEntity<String> createMessage(@Valid @RequestBody WebHookRequest webHookRequest) {
        var request = SendMessageRequest.builder()
                .queueUrl(asyncQueueUrl)
                .messageBody(webHookRequest.payload())
                .messageGroupId(webHookRequest.messageGroupId())
                .messageDeduplicationId(webHookRequest.messageDeduplicationId())
                .build();

        CompletableFuture<SendMessageResponse> messageResponse = sqsAsyncClient.sendMessage(request);

        messageResponse.thenAccept(response -> System.out.println("Message sent successfully and Id is " + response.messageId()))
                .exceptionally(error -> {
                    System.out.println("Message sent failed " + error.getMessage());
                    return null;
                });

        return ResponseEntity.ok("Message sending started successfully");
    }
}
