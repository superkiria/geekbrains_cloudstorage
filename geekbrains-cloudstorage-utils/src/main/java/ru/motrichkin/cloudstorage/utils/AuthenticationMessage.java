package ru.motrichkin.cloudstorage.utils;

public class AuthenticationMessage extends AbstractMessage implements ProcessingMessage {

    private String login;
    private String password;

    public AuthenticationMessage(String login, String password) {
        setAuthenticationMessage();
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        throw new RuntimeException("Method not supported exception");
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        throw new RuntimeException("Method not supported exception");
    }
}
