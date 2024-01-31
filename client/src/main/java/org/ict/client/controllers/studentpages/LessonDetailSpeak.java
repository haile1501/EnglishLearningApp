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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.UserContext;
import org.ict.client.models.Exercise;
import org.ict.client.models.Feedback;
import org.ict.client.models.LessonListItem;
import org.ict.client.utils.JSONUtil;

import javax.sound.sampled.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class LessonDetailSpeak {
    @FXML
    private Text title;

    @FXML
    private Button back;

    @FXML
    private Button previous;

    @FXML
    private Button submit;

    @FXML
    private Text topic;

    @FXML
    private Text mark;

    @FXML
    private Text comment;

    @FXML
    private Button record;

    @FXML
    private Text fileName;

    @FXML
    private Button replay;

    private boolean isRecording = false;

    private LessonListItem lessonListItem;

    private Exercise speakingExercise;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private TargetDataLine targetLine;
    private ByteArrayOutputStream byteArrayOutputStream;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;

    @FXML
    private void initialize() {
        mark.setVisible(false);
        comment.setVisible(false);
        replay.setVisible(false);
        fileName.setVisible(false);
        previous.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader2 = new FXMLLoader();
                String pathToFxml2 = "./src/main/resources/org/ict/client/studentpages/LessonDetailEssayEx.fxml";
                URL lessonItemUrl2 = new File(pathToFxml2).toURI().toURL();
                loader2.setLocation(lessonItemUrl2);
                root = loader2.load();
                LessonDetailEssayEx lessonDetailEssayEx = loader2.getController();
                lessonDetailEssayEx.setData(lessonListItem);
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
        replay.setOnMouseClicked(mouseEvent -> {
            if (isPlaying) {
                isPlaying = false;
                replay.setText("Replay");
                mediaPlayer.pause();
            } else {
                isPlaying = true;
                replay.setText("End replay");
                String path = STR."./src/main/resources/org/ict/client/audios/\{lessonListItem.getId()}\{UserContext.getInstance().getUser().getId()}.wav";

                //Instantiating Media class
                Media media = new Media(new File(path).toURI().toString());

                //Instantiating MediaPlayer class
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            }
        });
        submit.setOnMouseClicked(mouseEvent -> {
            SocketManager socketManager = SocketManager.getInstance();
            try {
                socketManager.sendMessage(STR."SUBMIT_SPEAK_EX-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}-\{JSONUtil.stringify(speakingExercise)}", this::handleDoneSubmitSpeak);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File audioFile = new File(STR."./src/main/resources/org/ict/client/audios/\{lessonListItem.getId()}\{UserContext.getInstance().getUser().getId()}.wav");
            byte[] buffer3 = new byte[4096];

            int count2;
            InputStream in2 = null;
            try {
                in2 = new FileInputStream(audioFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    if (!((count2 = in2.read(buffer3)) > 0)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    socketManager.getOutput().write(buffer3, 0, count2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            submit.setText("Submitted");
            submit.setDisable(true);
            record.setDisable(true);
        });
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
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
        record.setOnMouseClicked(mouseEvent -> {
            if (isRecording) {
                isRecording = false;
                record.setText("Start recording");
                replay.setVisible(true);
                fileName.setVisible(true);
                targetLine.stop();
                targetLine.close();

            } else {
                fileName.setText("");
                isRecording = true;
                record.setText("End recording");
                AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
                DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                try {
                    targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
                try {
                    targetLine.open(audioFormat);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
                targetLine.start();
                Thread captureThread = getThread();
                captureThread.start();
            }
        });
    }

    private Thread getThread() {
        File audioFile = new File(STR."./src/main/resources/org/ict/client/audios/\{lessonListItem.getId()}\{UserContext.getInstance().getUser().getId()}.wav");
        AudioInputStream ais = new AudioInputStream(targetLine);
        Thread captureThread = new Thread(() -> {
            try {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return captureThread;
    }

    private void handleDoneSubmitSpeak(String message) {

    }

    public void setData(LessonListItem lessonListItem) {
        fileName.setText(STR."\{lessonListItem.getId()}\{UserContext.getInstance().getUser().getId()}.wav");
        this.lessonListItem = lessonListItem;
        title.setText(STR."\{lessonListItem.getTopic()} - \{lessonListItem.getLevel()}");
        SocketManager socketManager = SocketManager.getInstance();
        try {
            socketManager.sendMessage(STR."GET_LESSON_SPEAK-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}", this::handleReceiveSpeakEx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReceiveSpeakEx(String message) {
        Platform.runLater(() -> {
            String[] splitMessage = message.split("-");
            boolean isSubmitted = false;
            Feedback feedback;
            try {
                speakingExercise = JSONUtil.parse(splitMessage[0], Exercise.class);
                topic.setText(speakingExercise.getContent());
                if (splitMessage.length > 1) {
                    SocketManager socketManager = SocketManager.getInstance();
                    socketManager.sendMessage(STR."GET_SPEAK_WORK-\{lessonListItem.getId()}-\{UserContext.getInstance().getUser().getId()}", this::handleReceiveSpeakWork);
                    isSubmitted = true;
                    submit.setText("Submitted");
                    submit.setDisable(true);
                    record.setDisable(true);
                    feedback = JSONUtil.parse(splitMessage[1], Feedback.class);
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleReceiveSpeakWork(String message) {
        Platform.runLater(() -> {
            replay.setVisible(true);
            fileName.setVisible(true);
        });
    }
}
