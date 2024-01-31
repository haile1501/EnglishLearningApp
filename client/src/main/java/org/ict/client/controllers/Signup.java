package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.UserContext;
import org.ict.client.models.User;
import org.ict.client.models.dtos.SignupDto;
import org.ict.client.utils.JSONUtil;
import org.ict.client.utils.ResponseCode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Signup {

    @FXML
    private TextField loginId;

    @FXML
    private TextField password;

    @FXML
    private Button signupButton;

    @FXML
    private ChoiceBox<String> roleSelectList;

    @FXML
    private Text openLoginPage;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {
        loginId.setText("");
        password.setText("");
        roleSelectList.setItems(
                FXCollections.observableArrayList("Student", "Teacher"));
        roleSelectList.setValue("Student");

        openLoginPage.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/Login.fxml";
            URL loginUrl = null;
            try {
                loginUrl = new File(pathToFxml).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            loader.setLocation(loginUrl);
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });

        signupButton.setOnMouseClicked(mouseEvent -> {
            SignupDto signupDto = new SignupDto(loginId.getText(), password.getText(), roleSelectList.getValue().toLowerCase());
            SocketManager socketManager = SocketManager.getInstance();
            try {
                socketManager.sendMessage(STR."SIGNUP-\{JSONUtil.stringify(signupDto)}", this::handleSignupResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleSignupResponse(String message) {
        Platform.runLater(() -> {
            String[] splitMessage = message.split("-");
            int responseCode = Integer.parseInt(splitMessage[0]);

            if (responseCode == ResponseCode.SIGNUP_ERROR) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setHeight(200);
                dialog.setWidth(500);
                dialog.setTitle("Error");
                dialog.getDialogPane().setContent(new Label("Username already exists"));
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                // Wait for the user to close the dialog
                dialog.showAndWait();
            } else {
                Dialog<String> dialog = new Dialog<>();
                dialog.setHeight(200);
                dialog.setWidth(500);
                dialog.setTitle("Dialog Title");
                dialog.getDialogPane().setContent(new Label("Success"));
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                // Wait for the user to close the dialog
                dialog.showAndWait();
            }
        });
    }
}
