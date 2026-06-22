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
import java.util.regex.Pattern;

public class ChangePasswordController {

    @FXML private TextField loginField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    @FXML
    private void handleSave() {

        String login = loginField.getText().trim();
        String oldPassword = oldPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (login.isEmpty() || oldPassword.isEmpty()
                || newPassword.isEmpty() || confirmPassword.isEmpty()) {

            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Заполните все поля");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Новые пароли не совпадают");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText(
                    "Пароль должен содержать минимум 8 символов, " +
                            "заглавную букву, цифру и спецсимвол"
            );
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT user_id, password FROM users WHERE login = ?"
            );

            selectStmt.setString(1, login);

            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Пользователь не найден");
                return;
            }

            int userId = rs.getInt("user_id");
            String storedHash = rs.getString("password");

            if (!BCrypt.checkpw(oldPassword, storedHash)) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Неверный текущий пароль");
                return;
            }

            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE users SET password = ? WHERE user_id = ?"
            );

            updateStmt.setString(1, newHash);
            updateStmt.setInt(2, userId);

            updateStmt.executeUpdate();

            loginField.clear();
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Пароль успешно изменён");

        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/login.fxml")
            );

            Stage stage = (Stage) errorLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Ошибка возврата");
        }
    }
}