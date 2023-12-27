package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.daos.DAO;
import org.example.daos.ExerciseDAO;
import org.example.daos.ExerciseWorkDAO;
import org.example.models.Exercise;
import org.example.models.ExerciseWork;
import org.example.utils.JSONUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionProcess implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConnectionProcess.class);

    private final Socket socket;
    private final String clientId;
    private InputStream input;
    private OutputStream output;

    public ConnectionProcess(Socket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
        try {
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[2048];
            while (input.read(buffer) != -1) {
                String message = new String(buffer, StandardCharsets.UTF_8).trim();
                String[] splitMessage = message.split(" ");
                String command = splitMessage[0];
                switch (command) {
                    case "LOGIN":
                        String payloadString = splitMessage[1];
                        output.write("ok".getBytes());
                        logger.info(Arrays.toString(splitMessage));
                        break;
                    case "REGISTER":
                        break;
                    case "SET_LEVEL":
                        break;
                    case "GET_LESSON_LIST":
                        break;
                    case "GET_LESSON_DETAIL":
                        break;
                    case "GET_EXERCISE_LIST":
                        String payload = splitMessage[1];
                        List<Exercise> ex = new ExerciseDAO().getAll();
                        output.write(JSONUtil.stringify(ex).getBytes());
                        logger.info(JSONUtil.stringify(ex));
                        break;
                    case "SUBMIT_EXERCISE":
                        String submitExPayload = splitMessage[1];
                        for(Integer i = 2; i < splitMessage.length; i++){
                            submitExPayload = submitExPayload.concat(" " + splitMessage[i]);
                        }
                        ExerciseWorkDAO exWorkDAO = new ExerciseWorkDAO();
                        exWorkDAO.save(JSONUtil.parse(submitExPayload, ExerciseWork.class));
                        output.write("ok".getBytes());
                        break;
                    case "FEEDBACK_EXERCISE":
                        break;
                }
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage() {

    }
}
