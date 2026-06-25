package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Delivery;
import org.example.model.DeliveryPart;
import org.example.model.Part;
import javafx.beans.property.SimpleStringProperty;
import org.example.model.interfaces.IDeliveryPartDAO;
import org.example.model.DAO.DeliveryPartDAO;

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
    private final IDeliveryPartDAO deliveryPartDAO = new DeliveryPartDAO();
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
        partNameColumn.setCellValueFactory(cellData -> {
            String name = partNameMap.getOrDefault(cellData.getValue().getPartId(), "Неизвестно");
            return new SimpleStringProperty(name);
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
        for (Part part : deliveryPartDAO.getAllParts()) {
            partsList.add(part);
            partNameMap.put(part.getPartId(), part.getName());
        }
        partCombo.setItems(partsList);
    }

    private void loadDeliveryParts() {
        deliveryPartsList.clear();
        if (currentDelivery == null) {
            return;
        }
        deliveryPartsList.setAll(deliveryPartDAO.getDeliveryParts(currentDelivery.getDeliveryId()));
        partsTable.setItems(deliveryPartsList);
        try {
            deliveryPartDAO.updateDeliveryQuantity(currentDelivery.getDeliveryId(), deliveryPartsList);
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
            if (deliveryPartDAO.deliveryContainsPart(currentDelivery.getDeliveryId(), part.getPartId())) {
                errorLabel.setText("Эта деталь уже добавлена в поставку");
                return;
            }
            deliveryPartDAO.addDeliveryPart(currentDelivery.getDeliveryId(), part.getPartId(), quantity);
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
            deliveryPartDAO.deleteDeliveryPart(currentDelivery.getDeliveryId(), selected.getPartId());
            errorLabel.setText("");
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
            DeliveriesController controller = loader.getController();
            controller.setCurrentUserId(currentUserId);
            controller.setRoleName(roleName);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}