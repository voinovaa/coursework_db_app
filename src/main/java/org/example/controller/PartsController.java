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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PartsController {

    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> idColumn;
    @FXML private TableColumn<Part, String> nameColumn;
    @FXML private TableColumn<Part, String> articleColumn;
    @FXML private TextField nameField;
    @FXML private TextField articleField;
    @FXML private Label errorLabel;

    private ObservableList<Part> partsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("partId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("article"));

        partsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                articleField.setText(newVal.getArticle());
            }
        });

        loadParts();
    }

    private void loadParts() {
        partsList.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM parts");
            while (rs.next()) {
                partsList.add(new Part(
                        rs.getInt("part_id"),
                        rs.getString("name"),
                        rs.getString("article")
                ));
            }
            partsTable.setItems(partsList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String article = articleField.getText().trim();

        if (name.isEmpty() || article.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO parts (name, article) VALUES (?, ?)"
            );
            stmt.setString(1, name);
            stmt.setString(2, article);
            stmt.executeUpdate();
            clearFields();
            loadParts();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        Part selected = partsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите деталь");
            return;
        }

        String name = nameField.getText().trim();
        String article = articleField.getText().trim();

        if (name.isEmpty() || article.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE parts SET name = ?, article = ? WHERE part_id = ?"
            );
            stmt.setString(1, name);
            stmt.setString(2, article);
            stmt.setInt(3, selected.getPartId());
            stmt.executeUpdate();
            clearFields();
            loadParts();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Part selected = partsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите деталь");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM parts WHERE part_id = ?"
            );
            stmt.setInt(1, selected.getPartId());
            stmt.executeUpdate();
            clearFields();
            loadParts();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) partsTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        articleField.clear();
        errorLabel.setText("");
    }
}