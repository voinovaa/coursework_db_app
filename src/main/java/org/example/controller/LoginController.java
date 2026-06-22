package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.DAO.AuthDAO;
import org.example.model.AuthResult;
import org.example.model.interfaces.IAuthDAO;


public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final IAuthDAO authDAO = new AuthDAO();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }
        try {
            AuthResult result = authDAO.authenticate(login, password);
            if (result == null) {
                errorLabel.setText("Неверный логин или пароль");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainController controller = loader.getController();
            controller.setRole(result.getRoleName());
            controller.setUser(result.getUserId(), result.getLogin());
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