package org.ict.client.controllers.teacherpages;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.models.Exercise;
import org.ict.client.models.Feedback;
import org.ict.client.models.SubmissionListItem;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class EssaySubmission {

    @FXML
    private TextField mark;

    @FXML
    private TextArea comment;

    @FXML
    private Button submit;

    @FXML
    private Text topic;

    @FXML
    private TextArea answer;

    @FXML
    private Text title;

    @FXML
    private Button back;

    private Exercise essayExercise;
    private SubmissionListItem submissionListItem;

    private Feedback feedback;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {
        answer.setEditable(false);
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/teacherpages/ExerciseList.fxml";
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
            ExerciseList exerciseList = loader2.getController();
            try {
                exerciseList.setData("essay");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        submit.setOnMouseClicked(mouseEvent -> {
            SocketManager socketManager = SocketManager.getInstance();
            try {
                feedback.setComment(comment.getText());
                feedback.setScore(Integer.parseInt(mark.getText()));
                socketManager.sendMessage(STR."GIVE_FEEDBACK-\{JSONUtil.stringify(feedback)}", this::handleDoneFeedback);
                comment.setEditable(false);
                mark.setEditable(false);
                submit.setDisable(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleDoneFeedback(String message) {

    }

    public void setData(SubmissionListItem submissionListItem) throws IOException {
        this.submissionListItem = submissionListItem;
        title.setText(STR."\{submissionListItem.getStudent()}'s submission");
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_LESSON_ESSAY-\{submissionListItem.getLessonId()}-\{submissionListItem.getStudentId()}", this::handleReceiveEssayEx);
    }

    private void handleReceiveEssayEx(String message) {
        Platform.runLater(() -> {
            String[] splitMessage = message.split("-");
            try {
                essayExercise = JSONUtil.parse(splitMessage[0], Exercise.class);
                feedback = JSONUtil.parse(splitMessage[1], Feedback.class);
                answer.setText(essayExercise.getStudentWork());
                topic.setText(essayExercise.getContent());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        });
    }
}
