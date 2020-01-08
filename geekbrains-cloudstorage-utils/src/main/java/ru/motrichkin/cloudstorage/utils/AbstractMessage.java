package ru.motrichkin.cloudstorage.utils;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

}
