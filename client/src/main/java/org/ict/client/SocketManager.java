package org.ict.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketManager {

    private static final Logger logger = LogManager.getLogger(SocketManager.class);

    private static SocketManager instance;
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private final boolean isRunning = true;

    public interface MessageCallback {
        void onMessageReceived(String message) throws JsonProcessingException, ClassNotFoundException;
    }

    private MessageCallback messageCallback;

    private SocketManager() {

    }

    public static SocketManager getInstance() {
        if (instance == null) {
            synchronized (SocketManager.class) {
                if (instance == null) {
                    instance = new SocketManager();
                }
            }
        }

        return instance;
    }

    public void initializeConnection(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            startMessageReceiverThread();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
    }

    private void startMessageReceiverThread() {
        Thread messageReceiverThread = new Thread(() -> {
            try {
                while (isRunning) {
                    byte[] buffer = new byte[1024];
                    input.read(buffer);
                    String message = new String(buffer, StandardCharsets.UTF_8).trim();
                    messageCallback.onMessageReceived(message);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });

        messageReceiverThread.start();
    }

    public void sendMessage(String message, MessageCallback callback) throws IOException {
        this.messageCallback = callback;
        if (output != null) {
            output.write(message.getBytes());
        }
    }

    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            input.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }
}
