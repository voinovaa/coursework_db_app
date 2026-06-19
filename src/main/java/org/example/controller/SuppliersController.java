package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.database.DatabaseConnection;
import org.example.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SuppliersController {

    @FXML private TableView<Supplier> suppliersTable;
    @FXML private TableColumn<Supplier, Integer> idColumn;
    @FXML private TableColumn<Supplier, String> nameColumn;
    @FXML private TableColumn<Supplier, String> addressColumn;
    @FXML private TableColumn<Supplier, String> phoneColumn;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private Label errorLabel;

    private ObservableList<Supplier> suppliersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        suppliersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                addressField.setText(newVal.getAddress());
                phoneField.setText(newVal.getPhone());
            }
        });

        loadSuppliers();
    }

    private void loadSuppliers() {
        suppliersList.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM suppliers");
            while (rs.next()) {
                suppliersList.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone")
                ));
            }
            suppliersTable.setItems(suppliersList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO suppliers (name, address, phone) VALUES (?, ?, ?)"
            );
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, phone);
            stmt.executeUpdate();
            clearFields();
            loadSuppliers();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите поставщика");
            return;
        }

        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE suppliers SET name = ?, address = ?, phone = ? WHERE supplier_id = ?"
            );
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, phone);
            stmt.setInt(4, selected.getSupplierId());
            stmt.executeUpdate();
            clearFields();
            loadSuppliers();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите поставщика");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM suppliers WHERE supplier_id = ?"
            );
            stmt.setInt(1, selected.getSupplierId());
            stmt.executeUpdate();
            clearFields();
            loadSuppliers();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) suppliersTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        errorLabel.setText("");
    }
}