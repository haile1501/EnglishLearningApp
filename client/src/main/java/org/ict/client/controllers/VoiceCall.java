package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.models.User;
import org.ict.client.utils.JSONUtil;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.*;

public class VoiceCall {

    @FXML
    private Button endCall;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private User user;


    public void setData(User user) throws IOException {
        this.user = user;
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_ONLINE_USER_ADDRESS-\{JSONUtil.stringify(user)}", this::handleReceiveCalleeAddress);
    }

    private void handleReceiveCalleeAddress(String address) throws SocketException {
        if (address.equals("127.0.0.1")) {
            address = System.getenv("host");
        }
        DatagramSocket datagramSocket = SocketManager.getInstance().getDatagramSocket();
        SendVoiceThread sendVoiceThread = new SendVoiceThread(address, datagramSocket);
        ReceiveVoiceThread receiveVoiceThread = new ReceiveVoiceThread(datagramSocket);
        Thread sendThread = new Thread(sendVoiceThread);
        Thread receiveThread = new Thread(receiveVoiceThread);
        sendThread.start();
        receiveThread.start();
        endCall.setOnMouseClicked(mouseEvent -> {
            sendVoiceThread.stop();
            receiveVoiceThread.stop();
            FXMLLoader loader = new FXMLLoader();
            String pathToFxml = "./src/main/resources/org/ict/client/Conversation.fxml";
            URL url = null;
            try {
                url = new File(pathToFxml).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            loader.setLocation(url);
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Conversation conversation = loader.getController();
            try {
                conversation.setData(user, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        });
    }

    static class SendVoiceThread implements Runnable {
        private final String calleeAddress;

        private final DatagramSocket datagramSocket;

        private boolean isRunning = true;

        public void stop() {
            isRunning = false;
        }

        public SendVoiceThread(String calleeAddress, DatagramSocket datagramSocket) {
            this.calleeAddress = calleeAddress;
            this.datagramSocket = datagramSocket;
        }
        @Override
        public void run() {
            try {
                // Set up audio capture
                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[1024];
                DatagramPacket packet;

                while (isRunning) {
                    int count = line.read(buffer, 0, buffer.length);
                    packet = new DatagramPacket(buffer, count, InetAddress.getByName(this.calleeAddress), 1234);
                    datagramSocket.send(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ReceiveVoiceThread implements Runnable {
        private final DatagramSocket datagramSocket;

        private boolean isRunning = true;

        public void stop() {
            isRunning = false;
        }
        public ReceiveVoiceThread(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }
        @Override
        public void run() {
            try {
                // Set up audio playback
                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (isRunning) {
                    datagramSocket.receive(packet);
                    line.write(packet.getData(), 0, packet.getLength());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
