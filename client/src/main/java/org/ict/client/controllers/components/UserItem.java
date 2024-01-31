package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.controllers.Conversation;
import org.ict.client.models.User;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UserItem {

    @FXML
    private Text username;

    @FXML
    private Button chat;

    @FXML
    private Circle status;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private User user;

    @FXML
    protected void initialize() {

    }

    public void setData(User user, boolean isOnline) {
        this.user = user;
        username.setText(STR."\{user.getLoginId()} - \{user.getRole()}");
        status.setFill(isOnline ? Color.GREEN : Color.GRAY);
        chat.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/Conversation.fxml";
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
            Conversation conversation = loader.getController();
            try {
                conversation.setData(user, isOnline);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }
}
