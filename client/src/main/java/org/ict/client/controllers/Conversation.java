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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.UserContext;
import org.ict.client.controllers.components.MessageReceived;
import org.ict.client.controllers.components.MessageSent;
import org.ict.client.controllers.components.UserItem;
import org.ict.client.models.Message;
import org.ict.client.models.User;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

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

    @FXML
    private Button call;

    private User user;

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    protected void initialize() {
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.setChatCallback(this::handleReceiveMessage);
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
        send.setOnMouseClicked(mouseEvent -> {
            if (!messageInput.getText().trim().isEmpty()) {
                try {
                    socketManager.sendMessage(STR."SEND_MESSAGE-\{JSONUtil.stringify(user)}-\{messageInput.getText()}", this::handleMessageSent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FXMLLoader loader = new FXMLLoader();
                String pathToFxml = "./src/main/resources/org/ict/client/components/MessageSent.fxml";
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
                MessageSent messageSent = loader.getController();
                messageSent.setData(messageInput.getText());
                messageList.getChildren().add(pane);
                messageInput.clear();
            }
        });

        call.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/VoiceCall.fxml";
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

    private void handleMessageSent(String message) {

    }

    private void handleReceiveMessage(String message) {
        Platform.runLater(() -> {
            String[] splitMessage = message.split("-");
            try {
                User sender = JSONUtil.parse(splitMessage[1], User.class);
                if (Objects.equals(sender.getId(), this.user.getId())) {
                    String content = splitMessage[2];
                    FXMLLoader loader = new FXMLLoader();
                    String pathToFxml = "./src/main/resources/org/ict/client/components/MessageReceived.fxml";
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
                    MessageReceived messageReceived = loader.getController();
                    messageReceived.setData(content);
                    messageList.getChildren().add(pane);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleReceiveMessageList(String message) {
        Platform.runLater(() -> {
            try {
                List<Message> messages = JSONUtil.parseList(message, Message.class);
                for (Message message1 : messages) {
                    String pathToFxml;
                    FXMLLoader loader = new FXMLLoader();
                    if (message1.getSenderId() == UserContext.getInstance().getUser().getId()) {
                        pathToFxml = "./src/main/resources/org/ict/client/components/MessageSent.fxml";
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
                        MessageSent messageSent = loader.getController();
                        messageSent.setData(message1.getContent());
                        messageList.getChildren().add(pane);
                    } else {
                        pathToFxml = "./src/main/resources/org/ict/client/components/MessageReceived.fxml";
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
                        MessageReceived messageReceived = loader.getController();
                        messageReceived.setData(message1.getContent());
                        messageList.getChildren().add(pane);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
