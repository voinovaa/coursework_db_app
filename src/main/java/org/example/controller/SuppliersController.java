package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Supplier;
import org.example.model.interfaces.ISupplierDAO;
import org.example.model.DAO.SupplierDAO;

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
    private final ISupplierDAO supplierDAO = new SupplierDAO();
    private int currentUserId;
    private String roleName;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

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

        suppliersList.setAll(supplierDAO.getAllSuppliers());
        suppliersTable.setItems(suppliersList);
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
            supplierDAO.addSupplier(name, address, phone);
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
            supplierDAO.updateSupplier(selected.getSupplierId(), name, address, phone);
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
            supplierDAO.deleteSupplier(selected.getSupplierId());
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
            MainController controller = loader.getController();
            controller.setUser(currentUserId, "");
            controller.setRole(roleName);
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