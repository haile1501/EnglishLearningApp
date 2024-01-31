package org.ict.client.controllers.studentpages;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.models.LessonListItem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LessonDetailAudio {
    @FXML
    private Text title;

    @FXML
    private Button back;

    private boolean isPlaying = false;

    @FXML
    private Button play;

    @FXML
    private Button next;

    @FXML
    private Button previous;
    private LessonListItem lessonListItem;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private MediaPlayer mediaPlayer;

    @FXML
    protected void initialize() {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonList.fxml";
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
            mediaPlayer.stop();
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        previous.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader2 = new FXMLLoader();
                String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/LessonDetail.fxml";
                URL lessonItemUrl2 = new File(pathToFxml2).toURI().toURL();
                loader2.setLocation(lessonItemUrl2);
                root = loader2.load();
                LessonDetail lessonDetail = loader2.getController();
                mediaPlayer.stop();
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
        next.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonDetailVocab.fxml";
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
            LessonDetailVocab lessonDetailVocab = loader.getController();
            lessonDetailVocab.setData(lessonListItem);
            mediaPlayer.stop();
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }

    public void setData(LessonListItem lessonListItem) {
        this.lessonListItem = lessonListItem;
        title.setText(STR."\{lessonListItem.getTopic()} - \{lessonListItem.getLevel()}");
        SocketManager socketManager = SocketManager.getInstance();
        try {
            socketManager.sendMessage(STR."GET_LESSON_AUDIO-\{lessonListItem.getId()}", this::handleReceiveAudio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReceiveAudio(String message) {
        Platform.runLater(() -> {
            String path = "./src/main/resources/org/ict/client/audios/audio.mp3";

            //Instantiating Media class
            Media media = new Media(new File(path).toURI().toString());

            //Instantiating MediaPlayer class
            mediaPlayer = new MediaPlayer(media);

            //Instantiating MediaView class

            play.setOnMouseClicked(mouseEvent -> {
                if (isPlaying) {
                    play.setText("Play audio");
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    play.setText("Pause audio");
                    mediaPlayer.play();
                    isPlaying = true;
                }
            });
        });
    }
}
