package org.ict.client.controllers.studentpages;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.UserContext;
import org.ict.client.controllers.components.SentenceRewrite;
import org.ict.client.controllers.components.quiz.MultipleChoice;
import org.ict.client.models.Exercise;
import org.ict.client.models.Feedback;
import org.ict.client.models.LessonListItem;
import org.ict.client.models.QuizQuestion;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LessonDetailRewrite {
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
    private Text mark;

    @FXML
    private Text comment;

    @FXML
    private GridPane questionContainer;
    private LessonListItem lessonListItem;
    private List<Exercise> rewriteExerciseList;

    private Stage stage;
    private Scene scene;
    private Parent root;

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
                String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/LessonDetailQuiz.fxml";
                URL lessonItemUrl2 = new File(pathToFxml2).toURI().toURL();
                loader2.setLocation(lessonItemUrl2);
                root = loader2.load();
                LessonDetailQuiz lessonDetailVocab = loader2.getController();
                lessonDetailVocab.setData(lessonListItem);
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
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonDetailEssayEx.fxml";
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
            LessonDetailEssayEx lessonDetailEssayEx = loader.getController();
            lessonDetailEssayEx.setData(lessonListItem);
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
                SocketManager socketManager = SocketManager.getInstance();
                socketManager.sendMessage(STR."SUBMIT_REWRITE_EX-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}-\{JSONUtil.stringify(rewriteExerciseList)}", this::handleDoneSubmitRewrite);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setData(LessonListItem lessonListItem) {
        this.lessonListItem = lessonListItem;
        title.setText(STR."\{lessonListItem.getTopic()} - \{lessonListItem.getLevel()}");
        SocketManager socketManager = SocketManager.getInstance();
        try {
            socketManager.sendMessage(STR."GET_LESSON_REWRITE-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}", this::handleReceiveRewriteEx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReceiveRewriteEx(String message) {
        Platform.runLater(() -> {
            boolean isSubmitted = false;
            Feedback feedback;

            try {
                String[] splitMessage = message.split("-");
                if (splitMessage.length > 1) {
                    isSubmitted = true;
                    submit.setText("Submitted");
                    submit.setDisable(true);
                    feedback = JSONUtil.parse(splitMessage[1], Feedback.class);
                    if (feedback.getStatus().equals("waiting")) {
                        mark.setVisible(true);
                        mark.setText("Submitted. Waiting for evaluation.");
                    } else {
                        mark.setVisible(true);
                        mark.setText(STR."Mark: \{feedback.getScore()}");
                        comment.setVisible(true);
                        comment.setText(STR."Comment: \{feedback.getComment()}");
                    }
                }
                rewriteExerciseList = JSONUtil.parseList(splitMessage[0], Exercise.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            int row = 0;
            int column = 0;
            for (Exercise exercise: rewriteExerciseList) {
                FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/components/SentenceRewrite.fxml");
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                SentenceRewrite sentenceRewrite = loader.getController();
                sentenceRewrite.setData(exercise, isSubmitted);
                questionContainer.add(pane, column++, row);
                if (column == 2) {
                    column = 0;
                    row++;
                }
            }
        });
    }

    private void handleDoneSubmitRewrite(String message) {

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
