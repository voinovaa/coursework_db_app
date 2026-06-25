package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.AuthResult;
import org.example.model.interfaces.IAuthDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDAO implements IAuthDAO {

    @Override
    public AuthResult authenticate(String login, String password) {

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.user_id, u.password, r.name " +
                                    "FROM users u " +
                                    "JOIN user_roles ur ON u.user_id = ur.user_id " +
                                    "JOIN roles r ON ur.role_id = r.role_id " +
                                    "WHERE u.login = ? " +
                                    "ORDER BY r.role_id ASC " +
                                    "LIMIT 1");
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    AuthResult result = new AuthResult(rs.getInt("user_id"), login, rs.getString("name"));
                    rs.close();
                    stmt.close();
                    return result;
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register(String login, String password) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
        ResultSet countRs = countStmt.executeQuery();
        countRs.next();

        int userCount = countRs.getInt(1);
        countRs.close();
        countStmt.close();

        PreparedStatement insertUser = conn.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        insertUser.setString(1, login);
        insertUser.setString(2, passwordHash);
        insertUser.executeUpdate();

        ResultSet generatedKeys = insertUser.getGeneratedKeys();
        generatedKeys.next();
        int newUserId = generatedKeys.getInt(1);
        generatedKeys.close();
        insertUser.close();

        int roleId = (userCount == 0) ? 1 : 2;
        PreparedStatement insertRole = conn.prepareStatement("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)");
        insertRole.setInt(1, newUserId);
        insertRole.setInt(2, roleId);
        insertRole.executeUpdate();
        insertRole.close();
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement selectStmt = conn.prepareStatement("SELECT user_id, password FROM users WHERE login = ?");
        selectStmt.setString(1, login);
        ResultSet rs = selectStmt.executeQuery();
        if (!rs.next()) {
            rs.close();
            selectStmt.close();
            throw new Exception("Пользователь не найден");
        }
        int userId = rs.getInt("user_id");
        String storedHash = rs.getString("password");

        rs.close();
        selectStmt.close();

        if (!BCrypt.checkpw(oldPassword, storedHash)) {
            throw new Exception("Неверный текущий пароль");
        }

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password = ? WHERE user_id = ?");
        updateStmt.setString(1, newHash);
        updateStmt.setInt(2, userId);

        updateStmt.executeUpdate();

        updateStmt.close();
    }
}