package org.ict.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private static final Logger logger = LogManager.getLogger(HelloApplication.class);

    private static final String URL = System.getenv("host");
    private static final int PORT = Integer.parseInt(System.getenv("port"));

    public static void main(String[] args) {
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.initializeConnection(URL, PORT);
        launch();
    }
}