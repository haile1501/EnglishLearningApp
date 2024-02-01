package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class SentenceOrderingCard {

    @FXML
    private Text part;

    @FXML
    private AnchorPane pane;

    public void setData(String part) {
        this.part.setText(part);
    }

    public String getPart() {
        return this.part.getText();
    }

    public void hide() {
        this.pane.setVisible(false);
    }

    public void select() {
        pane.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    public void deselect() {
        pane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    }

}
