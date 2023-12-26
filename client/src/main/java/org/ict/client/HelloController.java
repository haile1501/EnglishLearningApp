package org.ict.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.ict.client.utils.JSONUtil;

import java.io.IOException;

public class HelloController {
    private SocketManager socketManager;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        welcomeText.setText("Welcome to JavaFX Application!");
        socketManager.sendMessage("LOGIN {\"username\":\"abc\",\"password\":\"abc\"}", this::handleDataResponse);
    }

    @FXML
    public void initialize() {
        socketManager = SocketManager.getInstance();
    }

    private void handleDataResponse(String data) {
        System.out.println(data);
    }

}