package ru.motrichkin.cloudstorage.utils.messages;

import ru.motrichkin.cloudstorage.utils.processing.MessageProcessingContext;
import ru.motrichkin.cloudstorage.utils.processing.MessageProcessingResult;
import ru.motrichkin.cloudstorage.utils.processing.ProcessingMessage;

public class TokenMessage extends AbstractMessage implements ProcessingMessage {

    public TokenMessage(String token) {
        this.setToken(token);
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext context) {
        return new MessageProcessingResult(new LogMessage("Wrong message type"));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext context) {
        System.out.println("Token received");
        MessageProcessingResult messageProcessingResult = new MessageProcessingResult();
        messageProcessingResult.setNewToken(this.getToken());
        return messageProcessingResult;
    }
}
