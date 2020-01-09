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
        if (incomingMessage instanceof ProcessingMessage) {
            if (incomingMessage.isAuthenticationMessage()) {
                AuthenticationMessage authenticationMessage = (AuthenticationMessage) incomingMessage;
                String token = null;
                try {
                    token = DatabaseServer.getToken(authenticationMessage.getLogin(), authenticationMessage.getPassword());
                } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return new LogMessage("Unexpected error");
                }
                return new TokenMessage(token);
            }
            String operatingFolderPath;
            try {
                String userFolderName = DatabaseServer.getFolderNameForToken(incomingMessage.getToken());
                if (userFolderName == null) {
                    return new LogMessage("No authentication");
                }
                operatingFolderPath = FOLDER + userFolderName;
            } catch (SQLException e) {
                e.printStackTrace();
                return new LogMessage("Unexpected error");
            }
            MessageProcessingContext messageProcessingContext = new MessageProcessingContext(operatingFolderPath);
            MessageProcessingResult result = ((ProcessingMessage) incomingMessage).processOnServer(messageProcessingContext);
            if (result != null) {
                return result.getAnswerMessage();
            }
            return new LogMessage("Unexpected error");
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
        if (incomingMessage instanceof FileRequestMessage) {
            return processCertainTypeMessage((FileRequestMessage) incomingMessage, folder);
        }
        if (incomingMessage instanceof FileMessage) {
            return processCertainTypeMessage((FileMessage) incomingMessage, folder);
        }
        return new LogMessage("Not valid message");
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

    private static AbstractMessage processCertainTypeMessage(FileMessage message, String folder) {
        try {
            Files.write(Paths.get(FOLDER + folder + "/" + message.getFilename()), message.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            return new LogMessage("Unexpected error");
        }
        return new LogMessage("The file was saved: " + message.getFilename());
    }

}
