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
import org.ict.client.controllers.components.SentenceOrderingCard;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class SentenceOrderingGame {

    @FXML
    private GridPane cardContainer;

    @FXML
    private Text title;

    @FXML
    private Button back;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private int level;

    private long startTime;

    private long endTime;
    private int pairedCard = 0;

    private final HashMap<String, Boolean> correctSentences = new HashMap<>();

    private SentenceOrderingCard pos1 = null;
    private SentenceOrderingCard pos2 = null;
    private SentenceOrderingCard pos3 = null;

    @FXML
    protected void initialize() {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/studentpages/GameLevelList.fxml");
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            GameLevelList gameLevelList = loader.getController();
            try {
                gameLevelList.setData("sentence");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }

    public void setData(int level) throws IOException {
        this.level = level;
        this.title.setText(STR."Sentence Ordering - Level \{level}");
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_SENTENCE_GAME-\{level}", this::handleReceiveGame);
    }

    private void handleReceiveGame(String message) {
        Platform.runLater(() -> {
            try {
                startTime = System.currentTimeMillis();
                String[] sentences = JSONUtil.parseList(message, String.class).toArray(new String[0]);
                boolean[][] pos = new boolean[8][3];
                for (int i = 0; i < 8; i++) {
                    correctSentences.put(sentences[i], true);
                    String[] parts = sentences[i].split("/");
                    for (int j = 0; j < 3; j++) {
                        int row;
                        Random random = new Random();
                        while (true) {
                            row = random.nextInt(8);
                            if (!pos[row][j]) {
                                pos[row][j] = true;
                                FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/components/SentenceOrderingCard.fxml");
                                AnchorPane pane = null;
                                try {
                                    pane = loader.load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                SentenceOrderingCard sentenceOrderingCard = loader.getController();
                                sentenceOrderingCard.setData(parts[j]);
                                int finalJ = j;
                                pane.setOnMouseClicked(mouseEvent -> {
                                    switch (finalJ) {
                                        case 0:
                                            if (pos1 != null) {
                                                pos1.deselect();
                                            }
                                            sentenceOrderingCard.select();
                                            pos1 = sentenceOrderingCard;
                                            break;
                                        case 1:
                                            if (pos2 != null) {
                                                pos2.deselect();
                                            }
                                            sentenceOrderingCard.select();
                                            pos2 = sentenceOrderingCard;
                                            break;
                                        case 2:
                                            if (pos3 != null) {
                                                pos3.deselect();
                                            }
                                            sentenceOrderingCard.select();
                                            pos3 = sentenceOrderingCard;
                                            break;
                                    }
                                    if (pos1 != null && pos2 != null && pos3 != null) {
                                        if (correctSentences.containsKey(STR."\{pos1.getPart()}/\{pos2.getPart()}/\{pos3.getPart()}")) {
                                            pairedCard++;
                                            pos1.hide();
                                            pos2.hide();
                                            pos3.hide();
                                            pos1 = null;
                                            pos2 = null;
                                            pos3 = null;
                                            if (pairedCard == 8) {
                                                endTime = System.currentTimeMillis();
                                                long elapsedTime = endTime - startTime;
                                                // Convert milliseconds to minutes and seconds
                                                long minutes = elapsedTime / (60 * 1000);
                                                long seconds = (elapsedTime / 1000) % 60;
                                                Dialog<String> dialog = new Dialog<>();
                                                dialog.setTitle("Complete!");
                                                dialog.getDialogPane().setContent(new Label(STR."Time:    \{minutes} min \{seconds} sec"));
                                                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                                // Wait for the user to close the dialog
                                                dialog.showAndWait();
                                                FXMLLoader loader2 = getFxmlLoader("./src/main/resources/org/ict/client/studentpages/GameLevelList.fxml");
                                                try {
                                                    root = loader2.load();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                GameLevelList gameLevelList = loader2.getController();
                                                try {
                                                    gameLevelList.setData("sentence");
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                scene = new Scene(root);
                                                stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                                                stage.setScene(scene);
                                                stage.show();
                                            }
                                        }
                                    }
                                });
                                cardContainer.add(pane, j, row);
                                break;
                            }
                        }
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
