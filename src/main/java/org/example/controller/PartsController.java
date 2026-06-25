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
import org.example.model.interfaces.IPartDAO;
import org.example.model.DAO.PartDAO;

public class PartsController {

    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> idColumn;
    @FXML private TableColumn<Part, String> nameColumn;
    @FXML private TableColumn<Part, String> articleColumn;
    @FXML private TextField nameField;
    @FXML private TextField articleField;
    @FXML private Label errorLabel;

    private ObservableList<Part> partsList = FXCollections.observableArrayList();
    private final IPartDAO partDAO = new PartDAO();
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
        partsList.setAll(partDAO.getAllParts());
        partsTable.setItems(partsList);
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
            partDAO.addPart(name, article);
            clearFields();
            loadParts();
            errorLabel.setText("");
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
            partDAO.updatePart(selected.getPartId(), name, article);
            clearFields();
            loadParts();
            errorLabel.setText("");
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
            partDAO.deletePart(selected.getPartId());
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
            MainController controller = loader.getController();
            controller.setUser(currentUserId, "");
            controller.setRole(roleName);
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