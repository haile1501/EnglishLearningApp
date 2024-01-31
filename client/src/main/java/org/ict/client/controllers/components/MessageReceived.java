package org.ict.client.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.ict.client.models.Message;

public class MessageReceived {

    @FXML
    private Text content;

    @FXML
    private Pane messageContainer;

    public void setData(String message) {
        messageContainer.setPrefWidth(message.length() * 8);
        this.content.setText(message);
    }
}
