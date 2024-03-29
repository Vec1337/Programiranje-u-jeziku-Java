package hr.javafx.domain.controllers;

import hr.javafx.domain.entities.User;
import hr.javafx.domain.enums.UserRole;
import hr.javafx.domain.exceptions.RequiredFieldsNotEnteredException;
import hr.javafx.domain.hashing.Encryptor;
import hr.javafx.domain.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AddUserScreenController {

    private static final Logger logger = LoggerFactory.getLogger(AddUserScreenController.class);


    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ComboBox<String> userRoleComboBox;

    public void initialize() {
        userRoleComboBox.getItems().add("ADMIN");
        userRoleComboBox.getItems().add("GUEST");
    }

    public void addUser() {
        Integer id = FileUtils.getNextUserId();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        String passwordHash = null;
        try {
            passwordHash = Encryptor.encryptString(password);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            System.out.println(e);
        }

        String roleString = userRoleComboBox.getValue();

        //GUEST DEFAULT
        UserRole role = UserRole.GUEST;

        if(roleString == "ADMIN") {
            role = UserRole.ADMIN;
        }

        User newUser = new User(id, username, passwordHash, role);

        try {

            if(username.isEmpty() || password.isEmpty()) {
                throw new RequiredFieldsNotEnteredException("Required fields are not entered!");
            }

            List<User> userList = FileUtils.readUsersFromFile();
            userList.add(newUser);
            FileUtils.saveUsersToFile(userList);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successfully added new user!");
            alert.setHeaderText("New user added!");
            alert.setContentText("User: " + username + " saved successfully.");

            alert.showAndWait();

        } catch (RequiredFieldsNotEnteredException e) {
            logger.error(e.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error while adding new user!");
            alert.setHeaderText("New user not added!");
            alert.setContentText("ERROR");

            alert.showAndWait();
        }




    }

}
