package org.ict.client.controllers.studentpages;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.UserContext;
import org.ict.client.models.Exercise;
import org.ict.client.models.Feedback;
import org.ict.client.models.LessonListItem;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LessonDetailEssayEx {

    @FXML
    private Text title;

    @FXML
    private Button back;

    @FXML
    private Button next;

    @FXML
    private Button previous;

    @FXML
    private Button submit;

    @FXML
    private Text topic;

    @FXML
    private TextArea answer;

    @FXML
    private Text mark;

    @FXML
    private Text comment;

    private LessonListItem lessonListItem;

    private Exercise writingExercise;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setData(LessonListItem lessonListItem) {
        this.lessonListItem = lessonListItem;
        title.setText(STR."\{lessonListItem.getTopic()} - \{lessonListItem.getLevel()}");
        SocketManager socketManager = SocketManager.getInstance();
        try {
            socketManager.sendMessage(STR."GET_LESSON_ESSAY-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}", this::handleReceiveEssayEx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        mark.setVisible(false);
        comment.setVisible(false);
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/studentpages/LessonList.fxml");
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
        previous.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader2 = new FXMLLoader();
                String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/LessonDetailRewrite.fxml";
                URL lessonItemUrl2 = new File(pathToFxml2).toURI().toURL();
                loader2.setLocation(lessonItemUrl2);
                root = loader2.load();
                LessonDetailRewrite lessonDetailRewrite = loader2.getController();
                lessonDetailRewrite.setData(lessonListItem);
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
        next.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonDetailSpeak.fxml";
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
            LessonDetailSpeak lessonDetailSpeak = loader.getController();
            lessonDetailSpeak.setData(lessonListItem);
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        submit.setOnMouseClicked(mouseEvent -> {
            try {
                submit.setText("submitted");
                submit.setDisable(true);
                mark.setVisible(true);
                mark.setText("Submitted. Waiting for evaluation.");
                answer.setEditable(false);
                writingExercise.setStudentWork(answer.getText());
                SocketManager socketManager = SocketManager.getInstance();
                socketManager.sendMessage(STR."SUBMIT_ESSAY_EX-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}-\{JSONUtil.stringify(writingExercise)}", this::handleDoneSubmitEssay);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleDoneSubmitEssay(String message) {

    }

    private void handleReceiveEssayEx(String message) {
        Platform.runLater(() -> {
            boolean isSubmitted = false;
            Feedback feedback;
            String[] splitMessage = message.split("-");
            try {
                writingExercise = JSONUtil.parse(splitMessage[0], Exercise.class);
                topic.setText(writingExercise.getContent());
                if (splitMessage.length > 1) {
                    isSubmitted = true;
                    submit.setText("submitted");
                    submit.setDisable(true);
                    feedback = JSONUtil.parse(splitMessage[1], Feedback.class);
                    answer.setText(writingExercise.getStudentWork());
                    answer.setEditable(false);
                    if (feedback.getStatus().equals("waiting")) {
                        mark.setVisible(true);
                        mark.setText("Submitted. Waiting for evaluation.");
                    } else {
                        mark.setVisible(true);
                        mark.setText(STR."Mark \{feedback.getScore()}");
                        comment.setVisible(true);
                        comment.setText(STR."Comment: \{feedback.getComment()}");
                    }
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static FXMLLoader getFxmlLoader(String pathToFxml) {
        FXMLLoader loader = new FXMLLoader();
        URL lessonItemUrl = null;
        try {
            lessonItemUrl = new File(pathToFxml).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        loader.setLocation(lessonItemUrl);
        return loader;
    }

}
