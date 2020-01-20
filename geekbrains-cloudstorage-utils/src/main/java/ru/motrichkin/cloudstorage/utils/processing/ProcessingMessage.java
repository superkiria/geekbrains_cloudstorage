package ru.motrichkin.cloudstorage.utils.processing;

public interface ProcessingMessage {
    MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext);
    MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext);
}
