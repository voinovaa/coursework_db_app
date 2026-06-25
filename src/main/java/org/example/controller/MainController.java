package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.model.Supplier;

public class MainController {

    @FXML private Button suppliersButton;
    @FXML private Button usersButton;

    private int userId;
    private String login;
    private String roleName;

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
            SuppliersController controller = loader.getController();
            controller.setCurrentUserId(userId);
            controller.setRoleName(roleName);
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
            PartsController controller = loader.getController();
            controller.setCurrentUserId(userId);
            controller.setRoleName(roleName);
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
            controller.setRoleName(roleName);
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
            PriceChangesController controller = loader.getController();
            controller.setCurrentUserId(userId);
            controller.setRoleName(roleName);
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
            UsersController controller = loader.getController();
            controller.setCurrentUserId(userId);
            controller.setRoleName(roleName);
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