package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.models.Message;
import org.ict.client.models.User;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Conversation {

    @FXML
    private Text username;

    @FXML
    private VBox messageList;

    @FXML
    private Button back;

    @FXML
    private Button send;

    @FXML
    private TextField messageInput;

    @FXML
    private Circle status;

    private User user;

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    protected void initialize() {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/Chat.fxml";
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
    }

    public void setData(User user, boolean isOnline) throws IOException {
        this.user = user;
        username.setText(STR."\{user.getLoginId()} - \{user.getRole()}");
        status.setFill(isOnline ? Color.GREEN : Color.GRAY);
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_MESSAGE_LIST-\{user.getId()}", this::handleReceiveMessageList);
    }

    private void handleReceiveMessageList(String message) {
        Platform.runLater(() -> {
            try {
                List<Message> messages = JSONUtil.parseList(message, Message.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
