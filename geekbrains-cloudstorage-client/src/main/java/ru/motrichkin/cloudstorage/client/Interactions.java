package ru.motrichkin.cloudstorage.client;

import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.nio.file.Paths;

public class Interactions {

    private static void receiveMessage() {
        try {
            Object object = Network.readObject();
            ClientMessageProcessor.process((AbstractMessage) object);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void authentificate(String login, String password) {
        AuthenticationMessage authenticationMessage = new AuthenticationMessage(login, password);
        Network.sendMessage(authenticationMessage);
        receiveMessage();
    }


    public static void sendFile(String fileName) {
        try {
            FileMessage fileMessage = new FileMessage(Paths.get(fileName));
            Network.sendMessage(fileMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiveMessage();
    }

    public static void removeFile(String fileName) {
        FileRemoveRequestMessage fileRemoveRequestMessage = new FileRemoveRequestMessage(fileName);
        Network.sendMessage(fileRemoveRequestMessage);
        receiveMessage();
    }

    public static void receiveFile(String fileName) {
        FileRequestMessage fileRequestMessage = new FileRequestMessage(fileName);
        Network.sendMessage(fileRequestMessage);
        receiveMessage();
    }

}
