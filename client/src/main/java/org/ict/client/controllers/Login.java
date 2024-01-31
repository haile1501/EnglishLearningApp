package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
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
import org.ict.client.models.dtos.LoginDto;
import org.ict.client.utils.JSONUtil;
import org.ict.client.utils.ResponseCode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class Login {

    @FXML
    private TextField loginId;

    @FXML
    private TextField password;

    @FXML
    private Button loginButton;

    @FXML
    private Text openSignupPage;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {
        loginId.setText("");
        password.setText("");
        loginButton.setOnMouseClicked(mouseEvent -> {
            SocketManager socketManager = SocketManager.getInstance();
            LoginDto loginDto = new LoginDto(loginId.getText(), password.getText());
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            try {
                socketManager.sendMessage(STR."LOGIN-\{JSONUtil.stringify(loginDto)}", this::handleLoginResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        openSignupPage.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/Signup.fxml";
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
    }

    private void handleLoginResponse(String message) throws JsonProcessingException {
        Platform.runLater(() -> {
            String[] splitMessage = message.split("-");
            int responseCode = Integer.parseInt(splitMessage[0]);

            if (responseCode == ResponseCode.LOGIN_ERROR) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setHeight(200);
                dialog.setWidth(500);
                dialog.setTitle("Error");
                dialog.getDialogPane().setContent(new Label("Wrong username or password"));
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                // Wait for the user to close the dialog
                dialog.showAndWait();
            } else {
                try {
                    User user = JSONUtil.parse(splitMessage[1], User.class);
                    UserContext.getInstance().initializeContext(user);
                    FXMLLoader loader = getFxmlLoader(user);
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static FXMLLoader getFxmlLoader(User user) {
        FXMLLoader loader = new FXMLLoader();
        String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonList.fxml";
        if (user.getRole().equals("teacher")) {
            pathToFxml = "./src/main/resources/org/ict/client/teacherpages/ExTypeSelection.fxml";
        }
        URL url = null;
        try {
            url = new File(pathToFxml).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        loader.setLocation(url);
        return loader;
    }
}
