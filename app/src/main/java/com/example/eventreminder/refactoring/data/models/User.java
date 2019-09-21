package com.example.eventreminder.refactoring.data.models;

import android.net.Uri;

public class User {

    private String Account;
    private String name;
    private String token;
    private Uri image;

    public User(String account, String name, String token, Uri image) {
        Account = account;
        this.name = name;
        this.token = token;
        this.image = image;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
