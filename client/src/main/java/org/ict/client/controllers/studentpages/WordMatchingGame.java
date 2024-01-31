package org.ict.client.controllers.studentpages;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.controllers.components.WordMatchingCardImage;
import org.ict.client.controllers.components.WordMatchingCardWord;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class WordMatchingGame {

    @FXML
    private Button back;

    @FXML
    private Text title;

    @FXML
    private GridPane cardContainer;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private int level;

    private int imageIndex = 0;

    private int pairedCard = 0;

    private String currentWord = "";

    private WordMatchingCardImage wordMatchingCardImage = null;

    private WordMatchingCardWord wordMatchingCardWord = null;

    private long startTime;

    private long endTime;

    @FXML
    protected void initialize() {
        back.setOnMouseClicked(mouseEvent -> {
            FXMLLoader loader = getFxmlLoader();
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            GameLevelList gameLevelList = loader.getController();
            try {
                gameLevelList.setData("word");
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
        this.title.setText(STR."Word Matching - Level \{level}");
        File folder = new File(STR."./src/main/resources/org/ict/client/images/word/l\{level}");
        folder.mkdir();
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_WORD_GAME_IMAGE-\{level}-\{imageIndex++}", this::handleReceiveGame);
    }

    private void handleReceiveGame(String message) {
        Platform.runLater(() -> {
            try {
                if (imageIndex == 8) {
                    startTime = System.currentTimeMillis();
                    boolean[][] pos = new boolean[4][4];
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            pos[i][j] = false;
                        }
                    }

                    File folder = new File(STR."./src/main/resources/org/ict/client/images/word/l\{level}");
                    File[] files = folder.listFiles();
                    for (File file: files) {
                        String word = file.getName().split("\\.")[0];
                        int row;
                        int column;
                        Random random = new Random();
                        while (true) {
                            row = random.nextInt(4);
                            column = random.nextInt(4);
                            if (!pos[row][column]) {
                                pos[row][column] = true;
                                FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/components/WordMatchingCardImage.fxml");
                                AnchorPane pane = null;
                                try {
                                    pane = loader.load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                WordMatchingCardImage wordMatchingCardImage = loader.getController();
                                wordMatchingCardImage.setData(level, word);
                                pane.setOnMouseClicked(mouseEvent -> {
                                    this.wordMatchingCardImage = wordMatchingCardImage;
                                    if (this.wordMatchingCardWord != null) {
                                        if (this.wordMatchingCardWord.getWord().equals(wordMatchingCardImage.getWord())) {
                                            this.wordMatchingCardWord.hide();
                                            this.wordMatchingCardImage.hide();
                                            pairedCard++;
                                            this.wordMatchingCardImage = null;
                                            this.wordMatchingCardWord = null;
                                            if (pairedCard == 8) {
                                                endTime = System.currentTimeMillis();
                                                long elapsedTime = endTime - startTime;
                                                // Convert milliseconds to minutes and seconds
                                                long minutes = elapsedTime / (60 * 1000);
                                                long seconds = (elapsedTime / 1000) % 60;
                                                Dialog<String> dialog = new Dialog<>();
                                                dialog.setTitle("Complete!");
                                                dialog.getDialogPane().setContent(new Label(STR."Time: \{minutes} : \{seconds}"));
                                                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                                // Wait for the user to close the dialog
                                                dialog.showAndWait();
                                                FXMLLoader loader2 = getFxmlLoader();
                                                try {
                                                    root = loader2.load();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                GameLevelList gameLevelList = loader2.getController();
                                                try {
                                                    gameLevelList.setData("word");
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
                                cardContainer.add(pane, column, row);
                                break;
                            }
                        }

                        while (true) {
                            row = random.nextInt(4);
                            column = random.nextInt(4);
                            if (!pos[row][column]) {
                                pos[row][column] = true;
                                FXMLLoader loader = getFxmlLoader("./src/main/resources/org/ict/client/components/WordMatchingCardWord.fxml");
                                AnchorPane pane = null;
                                try {
                                    pane = loader.load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                WordMatchingCardWord wordMatchingCardWord = loader.getController();
                                wordMatchingCardWord.setData(word);
                                pane.setOnMouseClicked(mouseEvent -> {
                                    this.wordMatchingCardWord = wordMatchingCardWord;
                                    if (this.wordMatchingCardImage != null) {
                                        if (this.wordMatchingCardWord.getWord().equals(wordMatchingCardImage.getWord())) {
                                            this.wordMatchingCardWord.hide();
                                            this.wordMatchingCardImage.hide();
                                            pairedCard++;
                                            this.wordMatchingCardImage = null;
                                            this.wordMatchingCardWord = null;
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
                                                FXMLLoader loader2 = getFxmlLoader();
                                                try {
                                                    root = loader2.load();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                GameLevelList gameLevelList = loader2.getController();
                                                try {
                                                    gameLevelList.setData("word");
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
                                cardContainer.add(pane, column, row);
                                break;
                            }
                        }
                    }
                    return;
                }
                SocketManager socketManager = SocketManager.getInstance();
                socketManager.sendMessage(STR."GET_WORD_GAME_IMAGE-\{level}-\{imageIndex++}", this::handleReceiveGame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private static FXMLLoader getFxmlLoader() {
        FXMLLoader loader2 = new FXMLLoader();
        String pathToFxml = "./src/main/resources/org/ict/client/studentpages/GameLevelList.fxml";
        URL url = null;
        try {
            url = new File(pathToFxml).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        loader2.setLocation(url);
        return loader2;
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
