package org.duder.model;

public class User {
    private String login;
    private String nickname;
    private String password;
    private String sessionToken;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String nickname, String password) {
        this.login = login;
        this.nickname = nickname;
        this.password = password;
    }

    public User(String login, String nickname, String password, String sessionToken) {
        this.login = login;
        this.nickname = nickname;
        this.password = password;
        this.sessionToken = sessionToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
