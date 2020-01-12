package ru.motrichkin.cloudstorage.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileRequestMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequestMessage(String filename) {
        this.filename = filename;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        String operatingFolder = messageProcessingContext.getOperatingFolder();
        if (Files.exists(Paths.get(operatingFolder+ "/" + this.getFilename()))) {
            FileMessage fileMessage = null;
            try {
                fileMessage = new FileMessage(Paths.get(operatingFolder + "/" + this.getFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new MessageProcessingResult(fileMessage);
        }
        return new MessageProcessingResult(new LogMessage("No such file found"));
    }


    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        return null;
    }
}