package org.ict.client.controllers.studentpages;

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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GameLevelList {

    @FXML
    private Button back;

    @FXML
    private VBox levelList;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String gameType;

    @FXML
    protected void initialize() {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/GamesSelection.fxml";
            URL logIn = null;
            try {
                logIn = new File(pathToFxml2).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            loader2.setLocation(logIn);
            try {
                root = loader2.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }

    public void setData(String gameType) throws IOException {
        this.gameType = gameType;
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_LEVEL_LIST-\{gameType}", this::handleReceiveLevelList);
    }

    private void handleReceiveLevelList(String message) {
        Platform.runLater(() -> {
            int totalLevel = Integer.parseInt(message);
            for (int i = 1; i <= totalLevel; i++) {
                FXMLLoader loader = new FXMLLoader();
                String pathToFxml = "./src/main/resources/org/ict/client/components/LevelItem.fxml";
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
                LevelItem levelItem = loader.getController();
                levelItem.setData(i, gameType);
                levelList.getChildren().add(pane);
            }
        });
    }
}
