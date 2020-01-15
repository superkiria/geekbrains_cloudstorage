package ru.motrichkin.cloudstorage.client;

import ru.motrichkin.cloudstorage.utils.*;

public class ClientMessageProcessor {

    private static int processedCount = 0;

    public static MessageProcessingResult process(AbstractMessage incomingMessage) {
        ProcessingMessage processingMessage = (ProcessingMessage) incomingMessage;
        MessageProcessingContext messageProcessingContext = new MessageProcessingContext();
        MessageProcessingResult messageProcessingResult = processingMessage.processOnClient(messageProcessingContext);
        if (messageProcessingResult.getNewToken() != null) {
            Network.setToken(messageProcessingResult.getNewToken());
        }
        processedCount++;
        return messageProcessingResult;
    }

    public static int getProcessedCount() {
        return processedCount;
    }
}
