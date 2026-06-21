package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML private Button suppliersButton;
    @FXML private Button usersButton;

    private int userId;
    private String login;

    public void setRole(String roleName) {
        if (!roleName.equals("Администратор")) {
            usersButton.setVisible(false);
            usersButton.setManaged(false);
        }
    }

    public void setUser(int userId, String login) {
        this.userId = userId;
        this.login = login;
    }

    @FXML
    private void handleSuppliers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/suppliers.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleParts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/parts.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeliveries() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/deliveries.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            DeliveriesController controller = loader.getController();
            controller.setCurrentUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePriceChanges() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/price_changes.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/users.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            ProfileController controller = loader.getController();
            controller.setUser(userId, login);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) suppliersButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}