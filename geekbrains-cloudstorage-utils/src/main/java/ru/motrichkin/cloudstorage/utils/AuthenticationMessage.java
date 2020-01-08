package ru.motrichkin.cloudstorage.utils;

public class AuthenticationMessage extends AbstractMessage {

    private String login;
    private String password;

    public AuthenticationMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

}
