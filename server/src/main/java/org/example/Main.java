package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final List<ConnectionProcess> connectionList = new ArrayList<>();

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("port"));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //noinspection InfiniteLoopStatement
            while (true){
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected " + clientSocket.getInetAddress().getHostAddress());
                ConnectionProcess connectionProcess = new ConnectionProcess(clientSocket, String.valueOf(System.currentTimeMillis()));
                connectionList.add(connectionProcess);
                new Thread(connectionProcess).start();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}