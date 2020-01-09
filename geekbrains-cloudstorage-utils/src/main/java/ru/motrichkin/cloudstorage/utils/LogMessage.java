package ru.motrichkin.cloudstorage.utils;

public class LogMessage extends AbstractMessage implements ProcessingMessage {
    private String record;

    public LogMessage(String record) {
        this.record = record;
    }

    public String getRecord() {
        return this.record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        System.out.println(this.getRecord());
        return new MessageProcessingResult(new LogMessage("The log message was received"));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        System.out.println(this.getRecord());
        return new MessageProcessingResult();
    }
}