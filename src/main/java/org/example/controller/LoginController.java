package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.user_id, u.password, r.name " +
                            "FROM users u " +
                            "JOIN user_roles ur ON u.user_id = ur.user_id " +
                            "JOIN roles r ON ur.role_id = r.role_id " +
                            "WHERE u.login = ?"
            );
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String storedHash = rs.getString("password");

                if (BCrypt.checkpw(password, storedHash)) {

                    int userId = rs.getInt("user_id");
                    String roleName = rs.getString("name");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
                    Stage stage = (Stage) loginField.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));

                    MainController controller = loader.getController();
                    controller.setRole(roleName);
                    controller.setUser(userId, login);

                } else {
                    errorLabel.setText("Неверный логин или пароль");
                }

            } else {
                errorLabel.setText("Неверный логин или пароль");
            }
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText("Ошибка открытия формы регистрации");
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/change_password.fxml")
            );

            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

        } catch (Exception e) {
            errorLabel.setText("Ошибка открытия формы");
        }
    }
}