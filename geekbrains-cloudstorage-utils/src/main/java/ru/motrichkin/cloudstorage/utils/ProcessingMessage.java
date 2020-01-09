package ru.motrichkin.cloudstorage.utils;

public interface ProcessingMessage {
    MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext);
    MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext);
}
