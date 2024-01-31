package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.ict.client.models.Message;

public class MessageSent {

    @FXML
    private Text content;

    @FXML
    private Pane messageContainer;

    public void setData(String message) {
        if (message.length() * 8 > messageContainer.getMinWidth()) {
            messageContainer.setTranslateX(messageContainer.getMinWidth() - message.length() * 8);
        }
        messageContainer.setPrefWidth(message.length() * 8);
        this.content.setText(message);
    }
}
