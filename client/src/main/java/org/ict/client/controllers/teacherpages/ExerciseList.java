package org.ict.client.controllers.teacherpages;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.controllers.components.SubmissionItem;
import org.ict.client.models.SubmissionListItem;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ExerciseList {

    @FXML
    private VBox submissionList;

    @FXML
    private Button back;

    @FXML
    private Text title;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/teacherpages/ExTypeSelection.fxml";
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

    public void setData(String exType) throws IOException {
        switch (exType) {
            case "rewrite":
                title.setText("Sentence rewriting submissions");
                break;
            case "essay":
                title.setText("Paragraphs writing submissions");
                break;
            case "speak":
                title.setText("Speaking submissions");
                break;
        }
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_SUBMISSION_LIST-\{exType}", this::handleReceiveSubmissions);
    }

    private void handleReceiveSubmissions(String message) {
        Platform.runLater(() -> {
            List<SubmissionListItem> submissionListItems = null;
            try {
                submissionListItems = JSONUtil.parseList(message, SubmissionListItem.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            submissionList.getChildren().clear();
            for (SubmissionListItem submissionListItem: submissionListItems) {
                FXMLLoader loader = new FXMLLoader();
                String pathToFxml = "./src/main/resources/org/ict/client/components/SubmissionItem.fxml";
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
                SubmissionItem submissionItem = loader.getController();
                submissionItem.setData(submissionListItem);
                submissionList.getChildren().add(pane);
            }
        });
    }
}
