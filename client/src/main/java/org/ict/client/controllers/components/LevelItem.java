package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.controllers.studentpages.WordMatchingGame;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LevelItem {

    @FXML
    private Button play;

    @FXML
    private Text level;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {

    }

    public void setData(int level) {
        this.level.setText(STR."Level \{level}");
        play.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/WordMatchingGame.fxml";
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
            WordMatchingGame wordMatchingGame = loader2.getController();
            try {
                wordMatchingGame.setData(level);
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
