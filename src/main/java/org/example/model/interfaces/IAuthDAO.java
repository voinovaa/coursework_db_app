package org.example.model.interfaces;

import org.example.model.AuthResult;

public interface IAuthDAO {

    AuthResult authenticate(String login, String password);

    void register(String login, String password) throws Exception;

    void changePassword(String login, String oldPassword, String newPassword) throws Exception;
}