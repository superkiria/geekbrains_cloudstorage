package ru.motrichkin.cloudstorage.utils;

public class MessageProcessingContext {

    private String operatingFolder;

    public MessageProcessingContext() {
        this.operatingFolder = "";
    }

    public MessageProcessingContext(String operatingFolder) {
        this.operatingFolder = operatingFolder;
    }

    public String getOperatingFolder() {
        return operatingFolder;
    }
}
