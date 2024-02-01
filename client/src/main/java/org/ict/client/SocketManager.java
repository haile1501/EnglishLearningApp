package org.ict.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.StringTemplate.STR;

@Getter
@Setter
public class SocketManager {

    private static final Logger logger = LogManager.getLogger(SocketManager.class);

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private final boolean isRunning = true;

    private DatagramSocket datagramSocket;

    public interface MessageCallback {
        void onMessageReceived(String message) throws IOException, ClassNotFoundException;
    }

    public interface ChatCallback {
        void onMessageReceived(String message) throws IOException, ClassNotFoundException;
    }

    private MessageCallback messageCallback;
    private ChatCallback chatCallback;

    private SocketManager() {

    }

    private static final class InstanceHolder {
        private static final SocketManager instance = new SocketManager();
    }

    public static SocketManager getInstance() {

        return InstanceHolder.instance;
    }

    public void initializeConnection(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.datagramSocket = new DatagramSocket(1234);
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
                    byte[] buffer = new byte[4096];
                    input.read(buffer);
                    String message = new String(buffer, StandardCharsets.UTF_8).trim();
                    if (message.contains("START_SEND_FILE")) {
                        String[] parts = message.split("-");
                        String type = parts[1];
                        String fileName = parts[2];
                        int fileSize = Integer.parseInt(parts[3]);
                        FileOutputStream fos = new FileOutputStream(STR."./src/main/resources/org/ict/client/\{type}/\{fileName}");
                        byte[] fileBuffer = new byte[4096];
                        int count;
                        int receivedSize = 0;
                        while ((count = input.read(fileBuffer)) != -1) {
                            receivedSize += count;
                            fos.write(fileBuffer, 0, count);
                            if (receivedSize == fileSize) {
                                break;
                            }
                        }
                        fos.close();
                    }
                    if (message.contains("RECEIVE_MESSAGE")) {
                        chatCallback.onMessageReceived(message);
                        continue;
                    }
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
