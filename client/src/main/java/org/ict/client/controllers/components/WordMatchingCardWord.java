package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WordMatchingCardWord {

    @FXML
    private Text word;

    public void setData(String word) {
        this.word.setText(word);
    }

    public void hide() {
        word.setVisible(false);
    }

    public String getWord() {
        return this.word.getText();
    }
}
