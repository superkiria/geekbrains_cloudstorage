package ru.motrichkin.cloudstorage.utils;

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
        System.out.println("Token: " + this.getToken());
        return new MessageProcessingResult();
    }
}
