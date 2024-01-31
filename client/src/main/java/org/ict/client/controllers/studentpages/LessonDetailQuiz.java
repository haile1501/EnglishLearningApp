package org.ict.client.controllers.studentpages;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.controllers.components.LessonItem;
import org.ict.client.controllers.components.quiz.FillInBlank;
import org.ict.client.controllers.components.quiz.MultipleChoice;
import org.ict.client.controllers.components.quiz.SentenceArrangement;
import org.ict.client.models.LessonListItem;
import org.ict.client.models.QuizQuestion;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LessonDetailQuiz {
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
    private GridPane quizzContainer;
    private LessonListItem lessonListItem;
    private List<QuizQuestion> quizQuestionList;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() {
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
                String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/LessonDetailVocab.fxml";
                URL lessonItemUrl2 = new File(pathToFxml2).toURI().toURL();
                loader2.setLocation(lessonItemUrl2);
                root = loader2.load();
                LessonDetailVocab lessonDetailVocab = loader2.getController();
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
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonDetailRewrite.fxml";
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
            LessonDetailRewrite lessonDetailRewrite = loader.getController();
            lessonDetailRewrite.setData(lessonListItem);
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        submit.setOnMouseClicked(mouseEvent -> {
            try {
                SocketManager socketManager = SocketManager.getInstance();
                socketManager.sendMessage(STR."SUBMIT_QUIZ-\{lessonListItem.getId()}-\{JSONUtil.stringify(quizQuestionList)}", this::handleReceiveQuizResult);
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
            socketManager.sendMessage(STR."GET_LESSON_QUIZ-\{lessonListItem.getId()}", this::handleReceiveQuizQuestions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReceiveQuizResult(String message) {
        Platform.runLater(() -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Quiz result");
            dialog.getDialogPane().setContent(new Label(message));
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            // Wait for the user to close the dialog
            dialog.showAndWait();
        });
    }

    private void handleReceiveQuizQuestions(String message) {
        Platform.runLater(() -> {
            try {
                quizQuestionList = JSONUtil.parseList(message, QuizQuestion.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            int row = 0;
            int column = 0;
            for (QuizQuestion quizQuestion: quizQuestionList) {
                switch (quizQuestion.getType()) {
                    case "multiple":
                        FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/components/quiz/MultipleChoice.fxml");
                        AnchorPane pane = null;
                        try {
                            pane = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        MultipleChoice multipleChoice = loader.getController();
                        multipleChoice.setData(quizQuestion);
                        quizzContainer.add(pane, column++, row);
                        break;
                    case "fill":
                        loader = getFxmlLoader("./src/main/resources/org/ict/client/components/quiz/FillInBlank.fxml");
                        try {
                            pane = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        FillInBlank fillInBlank = loader.getController();
                        fillInBlank.setData(quizQuestion);
                        quizzContainer.add(pane, column++, row);
                        break;
                    case "arrange":
                        loader = getFxmlLoader("./src/main/resources/org/ict/client/components/quiz/SentenceArrangement.fxml");
                        try {
                            pane = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        SentenceArrangement sentenceArrangement = loader.getController();
                        sentenceArrangement.setData(quizQuestion);
                        quizzContainer.add(pane, column++, row);
                        break;
                    default:
                        break;
                }
                if (column == 2) {
                    column = 0;
                    row++;
                }
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
