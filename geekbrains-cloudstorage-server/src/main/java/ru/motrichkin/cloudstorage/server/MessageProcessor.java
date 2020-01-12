package ru.motrichkin.cloudstorage.server;

import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class MessageProcessor {

    private static final String FOLDER = "server_storage/";

    public static AbstractMessage process(AbstractMessage incomingMessage) {
        if (incomingMessage.isAuthenticationMessage()) { // обрабатываем запросы на аутентификацию, отправляем клиенту токен
            AuthenticationMessage authenticationMessage = (AuthenticationMessage) incomingMessage;
            String token = null;
            try {
                token = DatabaseServer.getToken(authenticationMessage.getLogin(), authenticationMessage.getPassword());
            } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new LogMessage("Unexpected error");
            }
            if (token == null) {
                return new LogMessage("Wrong login or password");
            }
            return new TokenMessage(token);
        }

        String operatingFolderPath;
        try { // тут делаем аутентификацию отдельного сообщения по токену
            String userFolderName = DatabaseServer.getFolderNameForToken(incomingMessage.getToken());
            if (userFolderName == null) {
                return new LogMessage("No authentication");
            }
            operatingFolderPath = FOLDER + userFolderName;
        } catch (SQLException e) {
            e.printStackTrace();
            return new LogMessage("Unexpected error");
        }

        if (Files.notExists(Paths.get(operatingFolderPath))) { // если у юзера нет директории, то создаём
            try {
                Files.createDirectory(Paths.get(operatingFolderPath));
            } catch (IOException e) {
                e.printStackTrace();
                return new LogMessage("Unexpected error");
            }
        }

        // обрабатываем сообщение
        MessageProcessingContext messageProcessingContext = new MessageProcessingContext(operatingFolderPath);
        MessageProcessingResult result = ((ProcessingMessage) incomingMessage).processOnServer(messageProcessingContext);
        if (result != null) {
            return result.getAnswerMessage();
        }
        return new LogMessage("Unexpected error");
    }

}
