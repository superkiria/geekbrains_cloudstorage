package ru.motrichkin.cloudstorage.utils;

public class LogMessage extends AbstractMessage {
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

}