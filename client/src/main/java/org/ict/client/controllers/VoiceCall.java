package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.ict.client.SocketManager;
import org.ict.client.models.User;
import org.ict.client.utils.JSONUtil;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class VoiceCall {

    @FXML
    private Button endCall;


    public void setData(User user) throws IOException {
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.sendMessage(STR."GET_ONLINE_USER_ADDRESS-\{JSONUtil.stringify(user)}", this::handleReceiveCalleeAddress);
    }

    private void handleReceiveCalleeAddress(String address) throws SocketException {
        if (address.equals("127.0.0.1")) {
            address = "192.168.1.33";
        }
        DatagramSocket datagramSocket = SocketManager.getInstance().getDatagramSocket();
        Thread sendThread = new Thread(new SendVoiceThread(address, datagramSocket));
        Thread receiveThread = new Thread(new ReceiveVoiceThread(datagramSocket));
        sendThread.start();
        receiveThread.start();
    }

    static class SendVoiceThread implements Runnable {
        private final String calleeAddress;

        private final DatagramSocket datagramSocket;

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

                while (true) {
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

                while (true) {
                    datagramSocket.receive(packet);
                    line.write(packet.getData(), 0, packet.getLength());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
