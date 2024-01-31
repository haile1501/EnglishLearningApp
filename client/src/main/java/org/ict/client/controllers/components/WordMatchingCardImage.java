package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.io.File;

@Getter
public class WordMatchingCardImage {

    @FXML
    private ImageView image;

    private String word;

    public void setData(int level, String word) {
        this.word = word;
        File file = new File(STR."./src/main/resources/org/ict/client/images/word/l\{level}/\{word}.jpg");
        Image image1 = new Image(file.toURI().toString());
        image.setImage(image1);
    }

    public void hide() {
        image.setVisible(false);
    }
}
