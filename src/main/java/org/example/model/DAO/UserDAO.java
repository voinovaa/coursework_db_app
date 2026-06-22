package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.User;
import org.example.model.interfaces.IUserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {

    @Override
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("login"),
                        rs.getString("password")
                ));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public String getUserRoles(int userId) {

        StringBuilder roles = new StringBuilder();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT r.name " +
                            "FROM roles r " +
                            "JOIN user_roles ur ON r.role_id = ur.role_id " +
                            "WHERE ur.user_id = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (roles.length() > 0) {
                    roles.append(", ");
                }
                roles.append(rs.getString("name"));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles.toString();
    }

    @Override
    public boolean userHasRole(int userId, int roleId) {

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_roles WHERE user_id = ? AND role_id = ?");

            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            rs.close();
            stmt.close();
            return exists;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void addRoleToUser(int userId, int roleId) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)");

        stmt.setInt(1, userId);
        stmt.setInt(2, roleId);

        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public int countRoles(int userId) {

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM user_roles WHERE user_id = ?");
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            stmt.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void removeRoleFromUser(int userId, int roleId) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM user_roles WHERE user_id = ? AND role_id = ?");

        stmt.setInt(1, userId);
        stmt.setInt(2, roleId);

        stmt.executeUpdate();
        stmt.close();
    }
}