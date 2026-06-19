package org.example.model;

public class User {

    private int userId;
    private String login;
    private String password;

    public User(int userId, String login, String password) {
        this.userId = userId;
        this.login = login;
        this.password = password;
    }

    public int getUserId() { return userId; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
}