package ru.motrichkin.cloudstorage.client;

import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClientMessageProcessor {

    public static void process(AbstractMessage incomingMessage) {
        if (incomingMessage instanceof LogMessage) {
            processCertainTypeMessage((LogMessage) incomingMessage);
            return;
        }
        if (incomingMessage instanceof TokenMessage) {
            processCertainTypeMessage((TokenMessage) incomingMessage);
            return;
        }
        if (incomingMessage instanceof FileMessage) {
            processCertainTypeMessage((FileMessage) incomingMessage);
            return;
        }
    }

    private static void processCertainTypeMessage(TokenMessage message) {
        Network.setToken(message.getToken());
        System.out.println("Token: " + message.getToken());
    }

    private static void processCertainTypeMessage(LogMessage message) {
        System.out.println("Server: " + message.getRecord());
    }

    private static void processCertainTypeMessage(FileMessage message) {
        try {
            Files.write(Paths.get(message.getFilename()), message.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client: file is received: " + message.getFilename());
    }

}
