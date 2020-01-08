package ru.motrichkin.cloudstorage.utils;

public class FileRemoveRequestMessage extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRemoveRequestMessage(String filename) {
        this.filename = filename;
    }
}
