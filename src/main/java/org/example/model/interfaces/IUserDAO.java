package org.example.model.interfaces;

import org.example.model.User;

import java.util.List;

public interface IUserDAO {

    List<User> getAllUsers();

    String getUserRoles(int userId);

    boolean userHasRole(int userId, int roleId);

    void addRoleToUser(int userId, int roleId) throws Exception;

    int countRoles(int userId);

    void removeRoleFromUser(int userId, int roleId) throws Exception;
}