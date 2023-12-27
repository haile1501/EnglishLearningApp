package org.ict.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.ict.client.controllers.ExerciseView;
import org.ict.client.models.Exercise;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class HelloController {
    private SocketManager socketManager;
    @FXML
    private Label welcomeText;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private List<Exercise> exData;

    @FXML
    protected void onHelloButtonClick(ActionEvent mouseEvent) throws IOException {
        welcomeText.setText("Welcome to JavaFX Application!");
        try {
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/exercise-view.fxml";
            URL dockItemURL2 = new File(pathToFxml2).toURI().toURL();
            loader2.setLocation(dockItemURL2);
            root = loader2.load();
            ExerciseView exController = loader2.getController();
            scene = new Scene(root);

            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        socketManager = SocketManager.getInstance();
    }

    private void handleDataResponse(String data) throws JsonProcessingException {
        exData = (List<Exercise>) JSONUtil.parseList(data, Exercise.class);
    }

}