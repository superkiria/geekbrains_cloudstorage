package ru.motrichkin.cloudstorage.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePartMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;
    private byte[] data;
    private int part;
    private int ofParts;

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public FilePartMessage(Path path, int part, int ofParts, byte[] data) throws IOException {
        filename = path.getFileName().toString();
        this.data = data;
        this.part = part;
        this.ofParts = ofParts;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        return null;
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        return null;
    }
}
