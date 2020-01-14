package ru.motrichkin.cloudstorage.client;

import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class Interactions {

    private static MessageProcessingResult receiveMessage() {
        AbstractMessage message = null;
        try {
            message = Network.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return ClientMessageProcessor.process(message);
    }

    public static boolean authenticate(String login, String password) {
        AuthenticationMessage authenticationMessage = new AuthenticationMessage(login, password);
        Network.sendMessage(authenticationMessage);
        return receiveMessage().getNewToken() != null;
    }


    public static void sendFile(String fileName) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            FileMessage fileMessage;
            int pos = 0;
            while (pos < file.length()) {
                int increment = Math.min(256, (int) file.length() - pos);
                fileMessage = new FileMessage(Paths.get(fileName), pos, increment, file.length());
                Network.sendMessage(fileMessage);
                receiveMessage();
                pos += increment;
                System.out.println(pos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void receiveFilesList() {
        Network.sendMessage(new FilesListRequestMessage());
        receiveMessage();
    }

    public static void renameFile(String oldName, String newName) {
        Network.sendMessage(new FileRenameRequestMessage(oldName, newName));
        receiveMessage();
    }

}
