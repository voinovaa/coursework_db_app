package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    @FXML
    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (login.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Пароли не совпадают");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errorLabel.setText("Пароль должен содержать минимум 8 символов, заглавную букву, цифру и спецсимвол");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            ResultSet countRs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            countRs.next();
            int userCount = countRs.getInt(1);

            PreparedStatement insertUser = conn.prepareStatement(
                    "INSERT INTO users (login, password) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            insertUser.setString(1, login);
            insertUser.setString(2, password);
            insertUser.executeUpdate();

            ResultSet generatedKeys = insertUser.getGeneratedKeys();
            generatedKeys.next();
            int newUserId = generatedKeys.getInt(1);

            int roleId = (userCount == 0) ? 1 : 2;

            PreparedStatement insertRole = conn.prepareStatement(
                    "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)"
            );
            insertRole.setInt(1, newUserId);
            insertRole.setInt(2, roleId);
            insertRole.executeUpdate();

            handleBack();
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate")) {
                errorLabel.setText("Пользователь с таким логином уже существует");
            } else {
                errorLabel.setText(e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText("Ошибка возврата к авторизации");
        }
    }
}