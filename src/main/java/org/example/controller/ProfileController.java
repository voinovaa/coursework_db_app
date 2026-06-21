package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Pattern;

public class ProfileController {

    @FXML private Label loginLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private int userId;
    private String login;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    public void setUser(int userId, String login) {
        this.userId = userId;
        this.login = login;
        loginLabel.setText("Логин: " + login);
    }

    @FXML
    private void handleSave() {
        String newPassword = newPasswordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("Заполните оба поля");
            return;
        }

        if (!newPassword.equals(confirm)) {
            errorLabel.setText("Пароли не совпадают");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            errorLabel.setText("Пароль должен содержать минимум 8 символов, заглавную букву, цифру и спецсимвол");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET password = ? WHERE user_id = ?"
            );
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            newPasswordField.clear();
            confirmPasswordField.clear();
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Пароль успешно изменён");
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) loginLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}