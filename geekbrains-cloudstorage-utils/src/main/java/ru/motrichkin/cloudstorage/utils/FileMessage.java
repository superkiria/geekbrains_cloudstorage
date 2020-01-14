package ru.motrichkin.cloudstorage.utils;

import java.io.IOException;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;
    private int position;
    private int length;
    private long fileSize;
    private String operatingFolder;

    public String getFilename() {
        return filename;
    }

    public FileMessage(Path path, int position, int length, long fileSize) throws IOException {
        filename = path.getFileName().toString();
        this.position = position;
        this.length = length;
        this.fileSize = fileSize;
    }

    public FileMessage(Path path, int position, int length, long fileSize, String operatingFolder) throws IOException {
        filename = path.getFileName().toString();
        this.position = position;
        this.length = length;
        this.fileSize = fileSize;
        this.operatingFolder = operatingFolder;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public String getOperatingFolder() {
        return operatingFolder;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        return new MessageProcessingResult(new LogMessage(this.getFilename() + ": " + ((this.position + this.length) / (this.fileSize / 100)) + "%"));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        System.out.println(this.getFilename() + ": " + ((this.position + this.length) / (this.fileSize / 100)) + "%");
        return new MessageProcessingResult(new LogMessage(this.getFilename() + ": " + ((this.position + this.length) / (this.fileSize / 100)) + "%"));
    }
}