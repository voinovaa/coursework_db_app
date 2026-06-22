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
import org.example.model.Delivery;
import org.example.model.DeliveryPart;
import org.example.model.Part;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DeliveryPartsController {

    @FXML private Label titleLabel;
    @FXML private TableView<DeliveryPart> partsTable;
    @FXML private TableColumn<DeliveryPart, String> partNameColumn;
    @FXML private TableColumn<DeliveryPart, Integer> quantityColumn;
    @FXML private ComboBox<Part> partCombo;
    @FXML private TextField quantityField;
    @FXML private Label errorLabel;

    private Delivery currentDelivery;
    private ObservableList<DeliveryPart> deliveryPartsList = FXCollections.observableArrayList();
    private ObservableList<Part> partsList = FXCollections.observableArrayList();
    private Map<Integer, String> partNameMap = new HashMap<>();

    @FXML
    public void initialize() {
        partNameColumn.setCellValueFactory(cellData -> {
            String name = partNameMap.getOrDefault(cellData.getValue().getPartId(), "Неизвестно");
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        partCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getArticle() + ")");
            }
        });
        partCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getArticle() + ")");
            }
        });

        loadParts();
    }

    public void setDelivery(Delivery delivery) {
        this.currentDelivery = delivery;
        titleLabel.setText("Состав поставки #" + delivery.getDeliveryId());
        loadDeliveryParts();
    }

    private void loadParts() {
        partsList.clear();
        partNameMap.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM parts");
            while (rs.next()) {
                int id = rs.getInt("part_id");
                String name = rs.getString("name");
                partsList.add(new Part(
                        id,
                        name,
                        rs.getString("article")
                ));
                partNameMap.put(id, name);
            }
            partCombo.setItems(partsList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void loadDeliveryParts() {
        deliveryPartsList.clear();
        if (currentDelivery == null) return;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM delivery_parts WHERE delivery_id = ?"
            );
            stmt.setInt(1, currentDelivery.getDeliveryId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deliveryPartsList.add(new DeliveryPart(
                        rs.getInt("delivery_id"),
                        rs.getInt("part_id"),
                        rs.getInt("quantity_parts")
                ));
            }
            partsTable.setItems(deliveryPartsList);
            updateDeliveryQuantity();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void updateDeliveryQuantity() {
        if (currentDelivery == null) return;

        int totalQuantity = 0;
        for (DeliveryPart dp : deliveryPartsList) {
            totalQuantity += dp.getQuantity();
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE deliveries SET quantity = ? WHERE delivery_id = ?"
            );
            stmt.setInt(1, totalQuantity);
            stmt.setInt(2, currentDelivery.getDeliveryId());
            stmt.executeUpdate();
        } catch (Exception e) {
            errorLabel.setText("Ошибка обновления количества: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (currentDelivery == null) {
            errorLabel.setText("Поставка не выбрана");
            return;
        }

        Part part = partCombo.getValue();
        String quantityText = quantityField.getText().trim();

        if (part == null || quantityText.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                errorLabel.setText("Количество должно быть больше 0");
                return;
            }

            for (DeliveryPart dp : deliveryPartsList) {
                if (dp.getPartId() == part.getPartId()) {
                    errorLabel.setText("Эта деталь уже добавлена в поставку");
                    return;
                }
            }

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO delivery_parts (delivery_id, part_id, quantity_parts) VALUES (?, ?, ?)"
            );
            stmt.setInt(1, currentDelivery.getDeliveryId());
            stmt.setInt(2, part.getPartId());
            stmt.setInt(3, quantity);
            stmt.executeUpdate();

            quantityField.clear();
            partCombo.setValue(null);
            errorLabel.setText("");
            loadDeliveryParts();
        } catch (NumberFormatException e) {
            errorLabel.setText("Количество должно быть числом");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (currentDelivery == null) {
            errorLabel.setText("Поставка не выбрана");
            return;
        }

        DeliveryPart selected = partsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите деталь для удаления");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM delivery_parts WHERE delivery_id = ? AND part_id = ?"
            );
            stmt.setInt(1, currentDelivery.getDeliveryId());
            stmt.setInt(2, selected.getPartId());
            stmt.executeUpdate();

            loadDeliveryParts();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/deliveries.fxml"));
            Stage stage = (Stage) partsTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}