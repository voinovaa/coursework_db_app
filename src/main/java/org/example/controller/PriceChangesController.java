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
import org.example.model.Part;
import org.example.model.PriceChange;
import org.example.model.Supplier;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        supplierColumn.setCellValueFactory(cellData -> {
            String name = supplierNameMap.getOrDefault(cellData.getValue().getSupplierId(), "Неизвестно");
            return new javafx.beans.property.SimpleStringProperty(name);
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

    private void loadSuppliers() {
        suppliersList.clear();
        supplierNameMap.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM suppliers");
            while (rs.next()) {
                int id = rs.getInt("supplier_id");
                String name = rs.getString("name");
                suppliersList.add(new Supplier(
                        id,
                        name,
                        rs.getString("address"),
                        rs.getString("phone")
                ));
                supplierNameMap.put(id, name);
            }
            supplierCombo.setItems(suppliersList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void loadPriceChanges() {
        priceChangesList.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM price_changes ORDER BY date DESC");
            while (rs.next()) {
                priceChangesList.add(new PriceChange(
                        rs.getInt("change_id"),
                        rs.getInt("part_id"),
                        rs.getInt("supplier_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBigDecimal("value")
                ));
            }
            priceChangesTable.setItems(priceChangesList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
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

            Connection conn = DatabaseConnection.getConnection();

            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM price_changes WHERE part_id = ? AND supplier_id = ? AND date = ?"
            );
            checkStmt.setInt(1, part.getPartId());
            checkStmt.setInt(2, supplier.getSupplierId());
            checkStmt.setDate(3, java.sql.Date.valueOf(date));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                errorLabel.setText("Запись на эту дату уже существует");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO price_changes (part_id, supplier_id, date, value) VALUES (?, ?, ?, ?)"
            );
            stmt.setInt(1, part.getPartId());
            stmt.setInt(2, supplier.getSupplierId());
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setBigDecimal(4, value);
            stmt.executeUpdate();


            clearFields();
            loadPriceChanges();
            loadParts();
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
            Connection conn = DatabaseConnection.getConnection();

            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM price_changes WHERE change_id = ?"
            );

            stmt.setInt(1, selected.getChangeId());
            stmt.executeUpdate();

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