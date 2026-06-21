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
import org.example.model.Role;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsersController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Label errorLabel;

    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private ObservableList<Role> rolesList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        roleColumn.setCellValueFactory(cellData -> {
            int userId = cellData.getValue().getUserId();
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT r.name FROM roles r " +
                                "JOIN user_roles ur ON r.role_id = ur.role_id " +
                                "WHERE ur.user_id = ?"
                );
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                StringBuilder roles = new StringBuilder();
                while (rs.next()) {
                    if (roles.length() > 0) roles.append(", ");
                    roles.append(rs.getString("name"));
                }
                return new javafx.beans.property.SimpleStringProperty(roles.toString());
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Ошибка");
            }
        });

        roleCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        roleCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        loadRoles();
        loadUsers();
    }

    private void loadRoles() {
        rolesList.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM roles");
            while (rs.next()) {
                rolesList.add(new Role(
                        rs.getInt("role_id"),
                        rs.getString("name")
                ));
            }
            roleCombo.setItems(rolesList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void loadUsers() {
        usersList.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");
            while (rs.next()) {
                usersList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("login"),
                        rs.getString("password")
                ));
            }
            usersTable.setItems(usersList);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleAddRole() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        Role role = roleCombo.getValue();

        if (selected == null) {
            errorLabel.setText("Выберите пользователя");
            return;
        }
        if (role == null) {
            errorLabel.setText("Выберите роль");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM user_roles WHERE user_id = ? AND role_id = ?"
            );
            checkStmt.setInt(1, selected.getUserId());
            checkStmt.setInt(2, role.getRoleId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                errorLabel.setText("У пользователя уже есть эта роль");
                return;
            }

            PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)"
            );
            insertStmt.setInt(1, selected.getUserId());
            insertStmt.setInt(2, role.getRoleId());
            insertStmt.executeUpdate();

            errorLabel.setText("");
            loadUsers();
            usersTable.refresh();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleRemoveRole() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        Role role = roleCombo.getValue();

        if (selected == null) {
            errorLabel.setText("Выберите пользователя");
            return;
        }
        if (role == null) {
            errorLabel.setText("Выберите роль");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            PreparedStatement countStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM user_roles WHERE user_id = ?"
            );
            countStmt.setInt(1, selected.getUserId());
            ResultSet countRs = countStmt.executeQuery();
            countRs.next();
            if (countRs.getInt(1) <= 1) {
                errorLabel.setText("У пользователя должна остаться хотя бы одна роль");
                return;
            }

            PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?"
            );
            deleteStmt.setInt(1, selected.getUserId());
            deleteStmt.setInt(2, role.getRoleId());
            deleteStmt.executeUpdate();

            errorLabel.setText("");
            loadUsers();
            usersTable.refresh();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}