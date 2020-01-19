package ru.motrichkin.cloudstorage.utils.messages;

import ru.motrichkin.cloudstorage.utils.processing.MessageProcessingContext;
import ru.motrichkin.cloudstorage.utils.processing.MessageProcessingResult;
import ru.motrichkin.cloudstorage.utils.processing.ProcessingMessage;

public class LogMessage extends AbstractMessage implements ProcessingMessage {
    private String record;

    public LogMessage(String record) {
        this.record = record;
    }

    public String getRecord() {
        return this.record;
    }


    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        System.out.println(this.getRecord());
        return new MessageProcessingResult(new LogMessage("The log message was received"));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        System.out.println("Server: " + this.getRecord());
        return new MessageProcessingResult();
    }
}