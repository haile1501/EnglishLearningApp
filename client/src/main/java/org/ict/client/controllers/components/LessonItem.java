package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.controllers.studentpages.LessonDetail;
import org.ict.client.models.LessonListItem;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LessonItem {

    @FXML
    private Text topic;

    private LessonListItem lessonListItem;

    @FXML
    private Button learn;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {
        learn.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader2 = new FXMLLoader();
                String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/LessonDetail.fxml";
                URL lessonItemUrl2 = new File(pathToFxml2).toURI().toURL();
                loader2.setLocation(lessonItemUrl2);
                root = loader2.load();
                LessonDetail lessonDetail = loader2.getController();
                lessonDetail.setData(lessonListItem);
                scene = new Scene(root);
                stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setUserData(lessonListItem.getId());
                stage.show();
            } catch (Exception e) {
                try {
                    throw e;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void setData(String topicData, LessonListItem lessonListItem) {
        topic.setText(topicData);
        this.lessonListItem = lessonListItem;
    }
}
