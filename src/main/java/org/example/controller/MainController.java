package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class MainController {

    @FXML private Button suppliersButton;

    @FXML
    private void handleSuppliers() {

    }

    @FXML
    private void handleParts() {

    }

    @FXML
    private void handleDeliveries() {

    }

    @FXML
    private void handlePriceChanges() {

    }

    @FXML
    private void handleUsers() {

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