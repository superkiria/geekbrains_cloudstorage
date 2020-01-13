package ru.motrichkin.cloudstorage.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        String operatingFolder = messageProcessingContext.getOperatingFolder();
        try {
            Files.write(Paths.get(operatingFolder + "/" + this.getFilename()), this.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageProcessingResult(new LogMessage("Unexpected error"));
        }
        return new MessageProcessingResult(new LogMessage("The file was saved: " + this.getFilename()));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        try {
            Files.write(Paths.get(this.getFilename()), this.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client: file is received: " + this.getFilename());
        return new MessageProcessingResult();
    }
}