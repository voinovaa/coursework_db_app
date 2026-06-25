package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Role;
import org.example.model.User;
import javafx.beans.property.SimpleStringProperty;
import org.example.model.interfaces.IUserDAO;
import org.example.model.DAO.UserDAO;
import org.example.model.interfaces.IRoleDAO;
import org.example.model.DAO.RoleDAO;

public class UsersController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Label errorLabel;

    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private ObservableList<Role> rolesList = FXCollections.observableArrayList();

    private final IUserDAO userDAO = new UserDAO();
    private final IRoleDAO roleDAO = new RoleDAO();
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        userDAO.getUserRoles(
                                cellData.getValue().getUserId()
                        )
                )
        );

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

        rolesList.setAll(roleDAO.getAllRoles());

        roleCombo.setItems(rolesList);
    }

    private void loadUsers() {

        usersList.setAll(userDAO.getAllUsers());

        usersTable.setItems(usersList);
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

            if (userDAO.userHasRole(
                    selected.getUserId(),
                    role.getRoleId()
            )) {
                errorLabel.setText("У пользователя уже есть эта роль");
                return;
            }
            userDAO.addRoleToUser(selected.getUserId(), role.getRoleId());
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
            if (userDAO.countRoles(selected.getUserId()) <= 1) {
                errorLabel.setText("У пользователя должна остаться хотя бы одна роль");
                return;
            }
            userDAO.removeRoleFromUser(selected.getUserId(), role.getRoleId());
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
            MainController controller = loader.getController();
            controller.setUser(currentUserId, "");
            controller.setRole(roleName);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}