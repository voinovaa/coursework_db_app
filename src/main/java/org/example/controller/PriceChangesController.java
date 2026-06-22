package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Part;
import org.example.model.PriceChange;
import org.example.model.Supplier;
import javafx.beans.property.SimpleStringProperty;

import org.example.model.interfaces.IPriceChangeDAO;
import org.example.model.DAO.PriceChangeDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PriceChangesController {

    @FXML private TableView<PriceChange> priceChangesTable;
    @FXML private TableColumn<PriceChange, Integer> idColumn;
    @FXML private TableColumn<PriceChange, String> partColumn;
    @FXML private TableColumn<PriceChange, String> supplierColumn;
    @FXML private TableColumn<PriceChange, LocalDate> dateColumn;
    @FXML private TableColumn<PriceChange, BigDecimal> valueColumn;
    @FXML private ComboBox<Part> partCombo;
    @FXML private ComboBox<Supplier> supplierCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField valueField;
    @FXML private Label errorLabel;

    private ObservableList<PriceChange> priceChangesList = FXCollections.observableArrayList();
    private ObservableList<Part> partsList = FXCollections.observableArrayList();
    private ObservableList<Supplier> suppliersList = FXCollections.observableArrayList();
    private Map<Integer, String> partNameMap = new HashMap<>();
    private Map<Integer, String> supplierNameMap = new HashMap<>();
    private final IPriceChangeDAO priceChangeDAO = new PriceChangeDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("changeId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        valueColumn.setCellFactory(column -> new TableCell<PriceChange, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        partColumn.setCellValueFactory(cellData -> {
            String name = partNameMap.getOrDefault(cellData.getValue().getPartId(), "Неизвестно");
            return new SimpleStringProperty(name);
        });

        supplierColumn.setCellValueFactory(cellData -> {
            String name = supplierNameMap.getOrDefault(cellData.getValue().getSupplierId(), "Неизвестно");
            return new SimpleStringProperty(name);
        });

        setupComboBoxes();
        loadParts();
        loadSuppliers();
        loadPriceChanges();
    }

    private void setupComboBoxes() {
        partCombo.setCellFactory(lv -> new ListCell<Part>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getArticle() + ")");
                }
            }
        });
        partCombo.setButtonCell(new ListCell<Part>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getArticle() + ")");
            }
        });

        supplierCombo.setCellFactory(lv -> new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        supplierCombo.setButtonCell(new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    private void loadParts() {
        partsList.clear();
        partNameMap.clear();
        for (Part part : priceChangeDAO.getAllParts()) {
            partsList.add(part);
            partNameMap.put(part.getPartId(), part.getName());
        }
        partCombo.setItems(partsList);
    }

    private void loadSuppliers() {
        suppliersList.clear();
        supplierNameMap.clear();
        for (Supplier supplier : priceChangeDAO.getAllSuppliers()) {
            suppliersList.add(supplier);
            supplierNameMap.put(supplier.getSupplierId(), supplier.getName());
        }
        supplierCombo.setItems(suppliersList);
    }

    private void loadPriceChanges() {
        priceChangesList.setAll(priceChangeDAO.getAllPriceChanges());
        priceChangesTable.setItems(priceChangesList);
    }

    @FXML
    private void handleAdd() {
        Part part = partCombo.getValue();
        Supplier supplier = supplierCombo.getValue();
        LocalDate date = datePicker.getValue();
        String valueText = valueField.getText().trim();

        if (part == null || supplier == null || date == null || valueText.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            BigDecimal value = new BigDecimal(valueText);
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                errorLabel.setText("Цена не может быть отрицательной");
                return;
            }
            if (priceChangeDAO.priceChangeExists(part.getPartId(), supplier.getSupplierId(), date)) {
                errorLabel.setText("Запись на эту дату уже существует");
                return;
            }
            priceChangeDAO.addPriceChange(part.getPartId(), supplier.getSupplierId(), date, value);
            clearFields();
            loadPriceChanges();
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Цена успешно обновлена");
        } catch (NumberFormatException e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Некорректный формат цены");
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        PriceChange selected = priceChangesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            errorLabel.setText("Выберите запись");
            return;
        }

        try {
            priceChangeDAO.deletePriceChange(selected.getChangeId());
            loadPriceChanges();
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Запись удалена");
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) priceChangesTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void clearFields() {
        partCombo.setValue(null);
        supplierCombo.setValue(null);
        datePicker.setValue(null);
        valueField.clear();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setText("");
    }
}