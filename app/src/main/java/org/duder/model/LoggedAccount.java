package org.duder.model;

public class LoggedAccount {
    private String login;
    private String nickname;
    private String password;
    private String sessionToken;
    private String imageUrl;

    public LoggedAccount() {
    }

    public LoggedAccount(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
