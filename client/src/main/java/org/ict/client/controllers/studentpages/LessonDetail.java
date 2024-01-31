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
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.models.LessonListItem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LessonDetail {

    @FXML
    private MediaView mediaView;

    @FXML
    private Text title;

    @FXML
    private Button back;

    private boolean isPlaying = false;

    @FXML
    private Button play;

    @FXML
    private Button next;

    private LessonListItem lessonListItem;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void initialize() throws IOException {
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
            mediaView.getMediaPlayer().stop();
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        next.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/studentpages/LessonDetailAudio.fxml";
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
            LessonDetailAudio lessonDetailAudio = loader.getController();
            lessonDetailAudio.setData(lessonListItem);
            mediaView.getMediaPlayer().stop();
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });

    }

    public void setData(LessonListItem lessonListItem) throws IOException {
        this.lessonListItem = lessonListItem;
        title.setText(STR."\{lessonListItem.getTopic()} - \{lessonListItem.getLevel()}");
        SocketManager socketManager = SocketManager.getInstance();
        try {
            socketManager.sendMessage(STR."GET_LESSON_VIDEO-\{lessonListItem.getId()}", this::handleReceiveVideo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReceiveVideo(String message) {
        Platform.runLater(() -> {
            String path = "./src/main/resources/org/ict/client/videos/video.mp4";

            //Instantiating Media class
            Media media = new Media(new File(path).toURI().toString());

            //Instantiating MediaPlayer class
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            //Instantiating MediaView class
            mediaView.setMediaPlayer(mediaPlayer);

            play.setOnMouseClicked(mouseEvent -> {
                if (isPlaying) {
                    play.setText("Play");
                    mediaView.getMediaPlayer().pause();
                    isPlaying = false;
                } else {
                    play.setText("Pause");
                    mediaView.getMediaPlayer().play();
                    isPlaying = true;
                }
            });
        });

    }
}
