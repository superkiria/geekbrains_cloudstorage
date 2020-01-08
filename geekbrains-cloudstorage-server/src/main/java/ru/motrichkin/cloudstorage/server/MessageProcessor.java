package ru.motrichkin.cloudstorage.server;

import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class MessageProcessor {

    private static final String FOLDER = "server_storage/";

    public static AbstractMessage process(AbstractMessage incomingMessage) {
        if (incomingMessage instanceof AuthenticationMessage) {
            return processCertainTypeMessage((AuthenticationMessage) incomingMessage);
        }
        if (incomingMessage instanceof LogMessage) {
            return processCertainTypeMessage((LogMessage) incomingMessage);
        }
        String folder = null;
        try {
            folder = DatabaseServer.getFolderNameForToken(incomingMessage.getToken());
        } catch (SQLException e) {
            e.printStackTrace();
            return new LogMessage("Unexpected error");
        }
        if (folder == null) {
            return new LogMessage("No authentication " + incomingMessage.getToken());
        }
        if (Files.notExists(Paths.get(FOLDER + folder))) {
            try {
                Files.createDirectory(Paths.get(FOLDER + folder));
            } catch (IOException e) {
                e.printStackTrace();
                return new LogMessage("Unexpected error");
            }
        }
        if (incomingMessage instanceof FileRemoveRequestMessage) {
            return processCertainTypeMessage((FileRemoveRequestMessage) incomingMessage, folder);
        }
        if (incomingMessage instanceof FileRequestMessage) {
            return processCertainTypeMessage((FileRequestMessage) incomingMessage, folder);
        }
        if (incomingMessage instanceof FileMessage) {
            return processCertainTypeMessage((FileMessage) incomingMessage, folder);
        }
        return new LogMessage("Not valid message");
    }

    private static AbstractMessage processCertainTypeMessage(AuthenticationMessage message) {
        TokenMessage tokenMessage = new TokenMessage();
        try {
            tokenMessage.setToken(DatabaseServer.getToken(message.getLogin(), message.getPassword()));
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new LogMessage("Unexpected error");
        }
        return tokenMessage;
    }

    private static AbstractMessage processCertainTypeMessage(FileRequestMessage message, String folder) {
        if (Files.exists(Paths.get(FOLDER + folder + "/" + message.getFilename()))) {
            FileMessage fileMessage = null;
            try {
                fileMessage = new FileMessage(Paths.get(FOLDER + folder + "/" + message.getFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileMessage;
        }
        return new LogMessage("No such file found");
    }

    private static AbstractMessage processCertainTypeMessage(LogMessage message) {
        System.out.println(message.getRecord());
        return new LogMessage("The log message was received");
    }

    private static AbstractMessage processCertainTypeMessage(FileMessage message, String folder) {
        try {
            Files.write(Paths.get(FOLDER + folder + "/" + message.getFilename()), message.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            return new LogMessage("Unexpected error");
        }
        return new LogMessage("The file was saved: " + message.getFilename());
    }

    private static AbstractMessage processCertainTypeMessage(FileRemoveRequestMessage message, String folder) {
        try {
            Files.deleteIfExists(Paths.get(FOLDER + folder + "/" + message.getFilename()));
        } catch (IOException e) {
            e.printStackTrace();
            return new LogMessage("No authentication");
        }
        return new LogMessage("The file was removed: " + message.getFilename());
    }



}
