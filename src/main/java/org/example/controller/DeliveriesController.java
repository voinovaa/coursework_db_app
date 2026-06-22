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
import org.example.database.DatabaseConnection;
import org.example.model.Delivery;
import org.example.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DeliveriesController {

    @FXML private TableView<Delivery> deliveriesTable;
    @FXML private TableColumn<Delivery, Integer> idColumn;
    @FXML private TableColumn<Delivery, String> supplierColumn;
    @FXML private TableColumn<Delivery, LocalDate> dateColumn;
    @FXML private TableColumn<Delivery, Integer> quantityColumn;
    @FXML private ComboBox<Supplier> supplierCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField quantityField;
    @FXML private Label errorLabel;
    private int currentUserId;

    private ObservableList<Delivery> deliveriesList = FXCollections.observableArrayList();
    private ObservableList<Supplier> suppliersList = FXCollections.observableArrayList();
    private Map<Integer, String> supplierNameMap = new HashMap<>();

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
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM suppliers");
            while (rs.next()) {
                int id = rs.getInt("supplier_id");
                String name = rs.getString("name");
                suppliersList.add(new Supplier(id, name, rs.getString("address"), rs.getString("phone")));
                supplierNameMap.put(id, name);
            }
            supplierCombo.setItems(suppliersList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void loadDeliveries() {
        deliveriesList.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM deliveries");
            while (rs.next()) {
                deliveriesList.add(new Delivery(
                        rs.getInt("delivery_id"),
                        rs.getInt("user_id"),
                        rs.getInt("supplier_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("quantity")
                ));
            }
            deliveriesTable.setItems(deliveriesList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
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
            Connection conn = DatabaseConnection.getConnection();

            int userId = currentUserId;

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO deliveries (user_id, supplier_id, date, quantity) VALUES (?, ?, ?, ?)"
            );
            stmt.setInt(1, userId);
            stmt.setInt(2, supplier.getSupplierId());
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
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
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM deliveries WHERE delivery_id = ?"
            );
            stmt.setInt(1, selected.getDeliveryId());
            stmt.executeUpdate();
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
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
}