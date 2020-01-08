package ru.motrichkin.cloudstorage.utils;

public class FileRequestMessage extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequestMessage(String filename) {
        this.filename = filename;
    }
}