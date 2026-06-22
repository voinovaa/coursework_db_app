package org.example.model;

public class AuthResult {

    private int userId;
    private String login;
    private String roleName;

    public AuthResult(int userId, String login, String roleName) {
        this.userId = userId;
        this.login = login;
        this.roleName = roleName;
    }

    public int getUserId() {return userId;}

    public String getLogin() {return login;}

    public String getRoleName() {return roleName;}
}