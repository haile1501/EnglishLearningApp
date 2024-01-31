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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.controllers.components.SentenceRewrite;
import org.ict.client.models.Exercise;
import org.ict.client.models.Feedback;
import org.ict.client.models.SubmissionListItem;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RewriteSubmission {

    @FXML
    private Button submit;

    @FXML
    private TextField mark;

    @FXML
    private TextArea comment;

    @FXML
    private Button back;

    @FXML
    private GridPane questionContainer;

    @FXML
    private Text title;

    private SubmissionListItem submissionListItem;
    private List<Exercise> rewriteExerciseList;

    private Feedback feedback;

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    protected void initialize() {
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
                exerciseList.setData("rewrite");
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
        socketManager.sendMessage(STR."GET_LESSON_REWRITE-\{submissionListItem.getLessonId()}-\{submissionListItem.getStudentId()}", this::handleReceiveRewriteEx);
    }

    private void handleReceiveRewriteEx(String message) {
        Platform.runLater(() -> {
            String[] splitMessage = message.split("-");
            try {
                rewriteExerciseList = JSONUtil.parseList(splitMessage[0], Exercise.class);
                feedback = JSONUtil.parse(splitMessage[1], Feedback.class);
                int row = 0;
                int column = 0;
                for (Exercise exercise: rewriteExerciseList) {
                    FXMLLoader loader = getFxmlLoader();
                    AnchorPane pane = null;
                    try {
                        pane = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SentenceRewrite sentenceRewrite = loader.getController();
                    sentenceRewrite.setData(exercise, true);
                    questionContainer.add(pane, column++, row);
                    if (column == 2) {
                        column = 0;
                        row++;
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static FXMLLoader getFxmlLoader() {
        FXMLLoader loader = new FXMLLoader();
        URL lessonItemUrl = null;
        try {
            lessonItemUrl = new File("./src/main/resources/org/ict/client/components/SentenceRewrite.fxml").toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        loader.setLocation(lessonItemUrl);
        return loader;
    }

}
