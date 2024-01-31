package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.controllers.teacherpages.EssaySubmission;
import org.ict.client.controllers.teacherpages.ExerciseList;
import org.ict.client.controllers.teacherpages.RewriteSubmission;
import org.ict.client.controllers.teacherpages.SpeakSubmission;
import org.ict.client.models.SubmissionListItem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SubmissionItem {

    @FXML
    private Text topic;

    @FXML
    private Button view;

    private SubmissionListItem submissionListItem;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {

    }

    public void setData(SubmissionListItem submissionListItem) {
        this.submissionListItem = submissionListItem;
        topic.setText(STR."\{submissionListItem.getLesson()} - \{submissionListItem.getStudent()}");
        view.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader2 = getFxmlLoader(submissionListItem);
            try {
                root = loader2.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (submissionListItem.getType()) {
                case "rewrite":
                    RewriteSubmission rewriteSubmission = loader2.getController();
                    try {
                        rewriteSubmission.setData(submissionListItem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "essay":
                    EssaySubmission essaySubmission = loader2.getController();
                    try {
                        essaySubmission.setData(submissionListItem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "speak":
                    SpeakSubmission speakSubmission = loader2.getController();
                    try {
                        speakSubmission.setData(submissionListItem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    break;
            }

            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }

    private static FXMLLoader getFxmlLoader(SubmissionListItem submissionListItem) {
        FXMLLoader loader2 = new FXMLLoader();
        String pathToFxml2 = "./src/main/resources/org/ict/client/teacherpages/RewriteSubmission.fxml";
        if (submissionListItem.getType().equals("essay")) {
            pathToFxml2 = "./src/main/resources/org/ict/client/teacherpages/EssaySubmission.fxml";
        } else if (submissionListItem.getType().equals("speak")) {
            pathToFxml2 = "./src/main/resources/org/ict/client/teacherpages/SpeakSubmission.fxml";
        }
        URL logIn = null;
        try {
            logIn = new File(pathToFxml2).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        loader2.setLocation(logIn);
        return loader2;
    }
}
