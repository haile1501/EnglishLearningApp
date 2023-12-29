package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.daos.*;
import org.example.models.*;
import org.example.models.dtos.conversation.ConversationSummary;
import org.example.models.dtos.message.GetConversationReqDto;
import org.example.models.dtos.message.SendMessageDto;
import org.example.utils.JSONUtil;
import org.example.models.dtos.GetLessonListDto;
import org.example.models.dtos.LoginDto;
import org.example.models.dtos.SignupDto;
import org.example.utils.ResponseCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
        ConversationDAO conversationDAO = ConversationDAO.getInstance();
        MessageDAO messageDAO = MessageDAO.getInstance();
        try {
            byte[] buffer = new byte[2048];
            while (input.read(buffer) != -1) {
                String message = new String(buffer, StandardCharsets.UTF_8).trim();
                String payloadString;
                String[] splitMessage = message.split(" ");
                String command = splitMessage[0];
                switch (command) {
                    case "LOGIN":
                        //log
                        logger.info(message);

                        payloadString = splitMessage[1];
                        LoginDto loginDto = JSONUtil.parse(payloadString, LoginDto.class);

                        UserDAO userDAO = UserDAO.getInstance();
                        Optional<User> user = userDAO.getUserByLoginId(loginDto.getLoginId());

                        if (user.isEmpty() || !user.get().getPassword().equals(loginDto.getPassword())) {
                            output.write(String.valueOf(ResponseCode.LOGIN_ERROR).getBytes());
                            break;
                        }
                        output.write((STR."\{String.valueOf(ResponseCode.OK)} \{JSONUtil.stringify(user.get())}").getBytes());
                        break;
                    case "SIGNUP":
                        //log
                        logger.info(message);

                        payloadString = splitMessage[1];
                        SignupDto signupDto = JSONUtil.parse(payloadString, SignupDto.class);

                        UserDAO userDAO2 = UserDAO.getInstance();
                        Optional<User> existingUser = userDAO2.getUserByLoginId(signupDto.getLoginId());

                        if (existingUser.isPresent()) {
                            output.write(String.valueOf(ResponseCode.SIGNUP_ERROR).getBytes());
                            break;
                        }

                        User newUser = new User();
                        newUser.setRole(signupDto.getRole());
                        newUser.setPassword(signupDto.getPassword());
                        newUser.setLoginId(signupDto.getLoginId());
                        userDAO2.save(newUser);
                        output.write(String.valueOf(ResponseCode.OK).getBytes());
                        break;
                    case "SET_LEVEL":
                        break;
                    case "GET_LESSON_LIST":
                        //log
                        logger.info(message);

                        payloadString = splitMessage[1];
                        GetLessonListDto getLessonListDto = JSONUtil.parse(payloadString, GetLessonListDto.class);
                        LessonDAO lessonDAO = LessonDAO.getInstance();
                        List<Lesson> lessonList = lessonDAO.getLessonListByTopicAndLevel(getLessonListDto.getTopic(), getLessonListDto.getLevel());

                        output.write(JSONUtil.stringify(lessonList).getBytes());
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

                    case "GET_CONVERSATIONS":

                        payloadString = splitMessage[1];
                        GetConversationReqDto getConversationReqDto = JSONUtil.parse(payloadString, GetConversationReqDto.class);

                        List<ConversationSummary> conversationSummaryList = conversationDAO.getAllConversationByAccountId(getConversationReqDto.getAccountId());
                        output.write(JSONUtil.stringify(conversationSummaryList).getBytes());
                        break;

                    case "SEND_MESSAGE":

                        payloadString = splitMessage[1];
                        SendMessageDto sendMessageDto = JSONUtil.parse(payloadString, SendMessageDto.class);

                        //add service check exists conversations between sender and receiver
                        Optional<Conversation> conversation = conversationDAO.getConversationByUserId(sendMessageDto.getSenderId(), sendMessageDto.getReceiverId());

                        Message newMessage = new Message();
                        newMessage.setSenderId(sendMessageDto.getSenderId());
                        newMessage.setReceiverId(sendMessageDto.getReceiverId());
                        newMessage.setContent(sendMessageDto.getContent());

                        if (conversation.isEmpty()) { // if conversation is empty
                            Long conversationId = conversationDAO.save(sendMessageDto.getSenderId(), sendMessageDto.getReceiverId());
                            newMessage.setConversationId(conversationId);
                        }
                        else {
                            Long conversationId = conversation.get().getId().longValue();
                            newMessage.setConversationId(conversationId);
                        }

                        messageDAO.save(newMessage);
                        output.write(String.valueOf(ResponseCode.OK).getBytes());
                        break;

                }
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage() {

    }
}
