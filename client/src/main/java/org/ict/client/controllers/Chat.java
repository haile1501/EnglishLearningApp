package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.controllers.components.LevelItem;
import org.ict.client.controllers.components.UserItem;
import org.ict.client.models.User;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Chat {

    @FXML
    private VBox userList;

    @FXML
    private Button back;

    private String[] onlineUsers;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    protected void initialize() throws IOException {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonList.fxml";
            URL url = null;
            try {
                url = new File(pathToFxml).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            loader.setLocation(url);
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
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage("GET_ONLINE_USERS", this::handleReceiveOnlineUsers);
    }

    private void handleReceiveOnlineUsers(String message) throws IOException {
        this.onlineUsers = JSONUtil.parseList(message, String.class).toArray(new String[0]);
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage("GET_USERS", this::handleReceiveUsers);
    }

    private void handleReceiveUsers(String message) {
        Platform.runLater(() -> {
            try {
                List<User> users = JSONUtil.parseList(message, User.class);
                for (User user: users) {
                    boolean found = false;
                    for (String onlineUser: onlineUsers) {
                        if (onlineUser.equals(user.getLoginId())) {
                            found = true;
                        }
                    }
                    FXMLLoader loader = new FXMLLoader();
                    String pathToFxml = "./src/main/resources/org/ict/client/components/UserItem.fxml";
                    URL lessonItemUrl = null;
                    try {
                        lessonItemUrl = new File(pathToFxml).toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    loader.setLocation(lessonItemUrl);
                    AnchorPane pane = null;
                    try {
                        pane = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    UserItem userItem = loader.getController();
                    userItem.setData(user, found);
                    userList.getChildren().add(pane);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
