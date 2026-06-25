package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Delivery;
import org.example.model.Supplier;
import org.example.model.interfaces.IDeliveryDAO;
import org.example.model.DAO.DeliveryDAO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DeliveriesController {

    @FXML
    private TableView<Delivery> deliveriesTable;
    @FXML
    private TableColumn<Delivery, Integer> idColumn;
    @FXML
    private TableColumn<Delivery, String> supplierColumn;
    @FXML
    private TableColumn<Delivery, LocalDate> dateColumn;
    @FXML
    private TableColumn<Delivery, Integer> quantityColumn;
    @FXML
    private ComboBox<Supplier> supplierCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField quantityField;
    @FXML
    private Label errorLabel;
    private int currentUserId;
    private String roleName;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    private ObservableList<Delivery> deliveriesList = FXCollections.observableArrayList();
    private ObservableList<Supplier> suppliersList = FXCollections.observableArrayList();
    private Map<Integer, String> supplierNameMap = new HashMap<>();
    private final IDeliveryDAO deliveryDAO = new DeliveryDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        supplierColumn.setCellValueFactory(cellData -> {
            String name = supplierNameMap.getOrDefault(cellData.getValue().getSupplierId(), "Неизвестно");
            return new SimpleStringProperty(name);
        });

        supplierCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        supplierCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        loadSuppliers();
        loadDeliveries();
    }

    private void loadSuppliers() {
        suppliersList.clear();
        supplierNameMap.clear();
        for (Supplier supplier : deliveryDAO.getAllSuppliers()) {
            suppliersList.add(supplier);
            supplierNameMap.put(supplier.getSupplierId(), supplier.getName());
        }
        supplierCombo.setItems(suppliersList);
    }

    private void loadDeliveries() {
        deliveriesList.setAll(deliveryDAO.getAllDeliveries());
        deliveriesTable.setItems(deliveriesList);
    }

    @FXML
    private void handleAdd() {
        Supplier supplier = supplierCombo.getValue();
        LocalDate date = datePicker.getValue();
        String quantityText = quantityField.getText().trim();

        if (supplier == null || date == null || quantityText.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            deliveryDAO.addDelivery(currentUserId, supplier.getSupplierId(), date, quantity);
            quantityField.clear();
            datePicker.setValue(null);
            supplierCombo.setValue(null);
            errorLabel.setText("");
            loadDeliveries();
        } catch (NumberFormatException e) {
            errorLabel.setText("Количество должно быть числом");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Delivery selected = deliveriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите поставку");
            return;
        }

        try {
            deliveryDAO.deleteDelivery(selected.getDeliveryId());
            errorLabel.setText("");
            loadDeliveries();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleViewParts() {
        Delivery selected = deliveriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите поставку");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/delivery_parts.fxml"));
            Stage stage = (Stage) deliveriesTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            DeliveryPartsController controller = loader.getController();
            controller.setDelivery(selected);
            controller.setCurrentUserId(currentUserId);
            controller.setRoleName(roleName);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }


    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) deliveriesTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainController controller = loader.getController();
            controller.setUser(currentUserId, "");
            controller.setRole(roleName);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}