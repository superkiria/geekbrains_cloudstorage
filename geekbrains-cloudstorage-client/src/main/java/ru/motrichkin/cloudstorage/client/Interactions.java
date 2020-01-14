package ru.motrichkin.cloudstorage.client;

import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class Interactions {

    public static void authenticate(String login, String password) {
        AuthenticationMessage authenticationMessage = new AuthenticationMessage(login, password);
        Network.sendMessage(authenticationMessage);
    }


    public static void sendFile(String fileName) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            FileMessage fileMessage;
            int pos = 0;
            while (pos < file.length()) {
                int increment = Math.min(1024 * 1024, (int) file.length() - pos);
                fileMessage = new FileMessage(Paths.get(fileName), pos, increment, file.length());
                Network.sendMessage(fileMessage);
                pos += increment;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFile(String fileName) {
        FileRemoveRequestMessage fileRemoveRequestMessage = new FileRemoveRequestMessage(fileName);
        Network.sendMessage(fileRemoveRequestMessage);
    }

    public static void receiveFile(String fileName) {
        FileRequestMessage fileRequestMessage = new FileRequestMessage(fileName);
        Network.sendMessage(fileRequestMessage);
    }

    public static void receiveFilesList() {
        Network.sendMessage(new FilesListRequestMessage());
    }

    public static void renameFile(String oldName, String newName) {
        Network.sendMessage(new FileRenameRequestMessage(oldName, newName));
    }

}
