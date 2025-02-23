package com.bsmlabs.app.controller;

public record WebHookRequest(String payload,
                             String messageGroupId,
                             String messageDeduplicationId) {
}
