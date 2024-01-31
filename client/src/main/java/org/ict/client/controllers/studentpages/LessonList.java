package org.ict.client.controllers.studentpages;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.controllers.components.LessonItem;
import org.ict.client.models.LessonListItem;
import org.ict.client.models.dtos.GetLessonListDto;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LessonList {
    @FXML
    private VBox lessonList;

    @FXML
    private TextField topic;

    @FXML
    private Text currentLevel;

    @FXML
    private ChoiceBox<String> level;

    @FXML
    private Button filter;

    @FXML
    private Button signOut;

    @FXML
    private Button game;

    @FXML
    private Button chat;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() throws IOException {
        topic.setText("");
        level.setItems(
                FXCollections.observableArrayList("Beginner", "Intermediate", "Advance"));
        level.setValue("Beginner");
        currentLevel.setText("Beginner");
        SocketManager socketManager = SocketManager.getInstance();
        GetLessonListDto getLessonListDto = new GetLessonListDto("", "Beginner");
        socketManager.sendMessage(STR."GET_LESSON_LIST-\{JSONUtil.stringify(getLessonListDto)}", this::handleLessonsResponse);

        filter.setOnMouseClicked(mouseEvent -> {
            GetLessonListDto getLessonListDto2 = new GetLessonListDto(topic.getText(), level.getValue());
            currentLevel.setText(level.getValue());
            try {
                socketManager.sendMessage(STR."GET_LESSON_LIST-\{JSONUtil.stringify(getLessonListDto2)}", this::handleLessonsResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        signOut.setOnMouseClicked(mouseEvent -> {
            try {
                socketManager.sendMessage("SIGN_OUT", this::handleSignOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/Login.fxml";
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
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });

        game.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/GamesSelection.fxml";
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
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        chat.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader2 = new FXMLLoader();
            String pathToFxml2 = "./src/main/resources/org/ict/client/Chat.fxml";
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
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }

    private void handleSignOut(String message) {

    }

    private void handleLessonsResponse(String message) throws IOException {
        Platform.runLater(() -> {
            List<LessonListItem> lessonListData = null;
            try {
                lessonListData = JSONUtil.parseList(message, LessonListItem.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                renderLessons(lessonListData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void renderLessons(List<LessonListItem> lessonListData) throws IOException {
        lessonList.getChildren().clear();
        for (LessonListItem lessonListItem: lessonListData) {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/components/LessonItem.fxml";
            URL lessonItemUrl = new File(pathToFxml).toURI().toURL();
            loader.setLocation(lessonItemUrl);
            AnchorPane pane = loader.load();
            LessonItem lessonItem = loader.getController();
            lessonItem.setData(lessonListItem.getTopic(), lessonListItem);
            lessonList.getChildren().add(pane);
        }
    }
}
