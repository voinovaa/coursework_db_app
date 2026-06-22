package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.DAO.AuthDAO;
import org.example.model.interfaces.IAuthDAO;

import java.util.regex.Pattern;

public class ChangePasswordController {

    @FXML private TextField loginField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    private final IAuthDAO authDAO = new AuthDAO();

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    @FXML
    private void handleSave() {
        String login = loginField.getText().trim();
        String oldPassword = oldPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        if (login.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
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
            errorLabel.setText("Пароль должен содержать минимум 8 символов, " +
                            "заглавную букву, цифру и спецсимвол");
            return;
        }
        try {
            authDAO.changePassword(login, oldPassword, newPassword);
            loginField.clear();
            oldPasswordField.clear();
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