package org.example;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.daos.*;
import org.example.models.*;
import org.example.models.dtos.*;
import org.example.utils.JSONUtil;
import org.example.utils.ResponseCode;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ConnectionProcess implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConnectionProcess.class);

    private final Socket socket;
    private final String clientId;
    private User user;
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
            byte[] buffer = new byte[4096];
            while (input.read(buffer) != -1) {
                String message = new String(buffer, StandardCharsets.UTF_8).trim();
                buffer = new byte[4096];
                String payloadString;
                String[] splitMessage = message.split("-");
                String command = splitMessage[0];
                logger.info(message);
                switch (command) {
                    case "LOGIN":

                        payloadString = splitMessage[1];
                        LoginDto loginDto = JSONUtil.parse(payloadString, LoginDto.class);

                        UserDAO userDAO = UserDAO.getInstance();
                        Optional<User> user = userDAO.getUserByLoginId(loginDto.getLoginId());

                        if (user.isEmpty() || !user.get().getPassword().equals(loginDto.getPassword())) {
                            sendMessage(String.valueOf(ResponseCode.LOGIN_ERROR));
                            break;
                        }
                        Main.onlineUsers.put(user.get().getLoginId(), this);
                        this.user = user.get();
                        sendMessage(STR."\{String.valueOf(ResponseCode.OK)}-\{JSONUtil.stringify(user.get())}");
                        break;
                    case "SIGNUP":


                        payloadString = splitMessage[1];
                        SignupDto signupDto = JSONUtil.parse(payloadString, SignupDto.class);

                        UserDAO userDAO2 = UserDAO.getInstance();
                        Optional<User> existingUser = userDAO2.getUserByLoginId(signupDto.getLoginId());

                        if (existingUser.isPresent()) {
                            sendMessage(String.valueOf(ResponseCode.SIGNUP_ERROR));
                            break;
                        }

                        User newUser = new User();
                        newUser.setRole(signupDto.getRole());
                        newUser.setPassword(signupDto.getPassword());
                        newUser.setLoginId(signupDto.getLoginId());
                        userDAO2.save(newUser);
                        sendMessage(String.valueOf(ResponseCode.OK));
                        break;
                    case "SET_LEVEL":
                        break;
                    case "GET_LESSON_LIST":


                        payloadString = splitMessage[1];
                        GetLessonListDto getLessonListDto = JSONUtil.parse(payloadString, GetLessonListDto.class);
                        LessonDAO lessonDAO = LessonDAO.getInstance();
                        List<Lesson> lessonList = lessonDAO.getLessonListByTopicAndLevel(getLessonListDto.getTopic(), getLessonListDto.getLevel());

                        sendMessage(JSONUtil.stringify(lessonList));
                        break;
                    case "GET_LESSON_CONTENT":
                        int lessonId = Integer.parseInt(splitMessage[1]);
                        LessonContentDAO lessonContentDAO = LessonContentDAO.getInstance();
                        List<LessonContent> lessonContentList = lessonContentDAO.getLessonContentsByLessonId(lessonId);
                        sendMessage(JSONUtil.stringify(lessonContentList));
                        break;
                    case "GET_LESSON_QUIZ":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        QuizQuestionDAO quizQuestionDAO = QuizQuestionDAO.getInstance();
                        List<QuizQuestion> quizQuestionList = quizQuestionDAO.getQuizQuestionsByLessonId(lessonId);
                        List<QuizQuestionDto> quizQuestionDtoList = quizQuestionList.stream().map(quizQuestion -> {
                            QuizQuestionDto quizQuestionDto = new QuizQuestionDto(quizQuestion.getType(),
                                    quizQuestion.getQuestion(), quizQuestion.getA(),
                                    quizQuestion.getB(), quizQuestion.getC(),
                                    quizQuestion.getD(), quizQuestion.getAnswer());
                            quizQuestionDto.setId(quizQuestion.getId());
                            quizQuestionDto.setCreatedAt(quizQuestion.getCreatedAt());
                            quizQuestionDto.setUpdatedAt(quizQuestion.getUpdatedAt());
                            return quizQuestionDto;
                        }).collect(Collectors.toList());
                        sendMessage(JSONUtil.stringify(quizQuestionDtoList));
                        break;
                    case "SUBMIT_QUIZ":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        List<QuizQuestionDto> submittedQuestions = JSONUtil.parseList(splitMessage[2], QuizQuestionDto.class);
                        quizQuestionList = QuizQuestionDAO.getInstance().getQuizQuestionsByLessonId(lessonId);
                        int score = 0;
                        for (int i = 0; i < submittedQuestions.size(); i++) {
                            if (submittedQuestions.get(i).getAnswer().equals(quizQuestionList.get(i).getCorrectAnswer())) {
                                score++;
                            }
                        }
                        sendMessage(STR."\{score}/\{submittedQuestions.size()}");
                        break;
                    case "GET_LESSON_VIDEO":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        File myFile = new File("./src/main/resources/videos/video.mp4");
                        output.write(STR."START_SEND_FILE-videos-\{myFile.getName()}-\{String.valueOf(myFile.length())}".getBytes());
                        int count;
                        byte[] buffer2 = new byte[4096];

                        InputStream in = new FileInputStream(myFile);
                        while ((count = in.read(buffer2)) > 0) {
                            output.write(buffer2, 0, count);
                        }
                        break;
                    case "GET_LESSON_AUDIO":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        File audioFile = new File("./src/main/resources/audios/audio.mp3");
                        output.write(STR."START_SEND_FILE-audios-\{audioFile.getName()}-\{String.valueOf(audioFile.length())}".getBytes());
                        byte[] buffer3 = new byte[4096];

                        int count2;
                        InputStream in2 = new FileInputStream(audioFile);
                        while ((count2 = in2.read(buffer3)) > 0) {
                            output.write(buffer3, 0, count2);
                        }
                        break;
                    case "GET_LESSON_REWRITE":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        int studentId = Integer.parseInt(splitMessage[2]);
                        List<Exercise> exerciseList = ExerciseDAO.getInstance().getRewriteExercisesByLessonId(lessonId);
                        Feedback feedback = FeedbackDAO.getInstance().findFeedback(studentId, lessonId, "rewrite");
                        List<ExerciseDto> exerciseDtoList = exerciseList.stream().map(exercise -> {
                                    ExerciseDto exerciseDto = new ExerciseDto(
                                            "",exercise.getContent(), exercise.getType()
                                    );
                                    exerciseDto.setId(exercise.getId());
                                    exerciseDto.setCreatedAt(exercise.getCreatedAt());
                                    exerciseDto.setUpdatedAt(exercise.getUpdatedAt());
                                    return exerciseDto;
                                }).
                                toList();
                        if (feedback != null) {
                            for (ExerciseDto exerciseDto : exerciseDtoList) {
                                ExerciseWork exerciseWork = ExerciseWorkDAO.getInstance().getWork(studentId, exerciseDto.getId());
                                exerciseDto.setStudentWork(exerciseWork.getWork());
                            }

                            sendMessage(STR."\{JSONUtil.stringify(exerciseDtoList)}-\{JSONUtil.stringify(feedback)}");
                            break;
                        }
                        sendMessage(STR."\{JSONUtil.stringify(exerciseDtoList)}");
                        break;
                    case "GET_LESSON_ESSAY":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        studentId = Integer.parseInt(splitMessage[2]);
                        Exercise exercise = ExerciseDAO.getInstance().getOneExercise(lessonId, "essay");
                        ExerciseDto exerciseDto1 = new ExerciseDto("", exercise.getContent(), exercise.getType());
                        exerciseDto1.setId(exercise.getId());
                        exerciseDto1.setCreatedAt(exercise.getCreatedAt());
                        exerciseDto1.setUpdatedAt(exercise.getUpdatedAt());
                        feedback = FeedbackDAO.getInstance().findFeedback(studentId, lessonId, "essay");
                        if (feedback != null) {
                            ExerciseWork exerciseWork = ExerciseWorkDAO.getInstance().getWork(studentId, exerciseDto1.getId());
                            exerciseDto1.setStudentWork(exerciseWork.getWork());
                            sendMessage(STR."\{JSONUtil.stringify(exerciseDto1)}-\{JSONUtil.stringify(feedback)}");
                            break;
                        }
                        sendMessage(JSONUtil.stringify(exerciseDto1));
                        break;
                    case "GET_LESSON_SPEAK":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        studentId = Integer.parseInt(splitMessage[2]);
                        exercise = ExerciseDAO.getInstance().getOneExercise(lessonId, "speak");
                        exerciseDto1 = new ExerciseDto("", exercise.getContent(), exercise.getType());
                        exerciseDto1.setId(exercise.getId());
                        exerciseDto1.setCreatedAt(exercise.getCreatedAt());
                        exerciseDto1.setUpdatedAt(exercise.getUpdatedAt());
                        feedback = FeedbackDAO.getInstance().findFeedback(studentId, lessonId, "speak");
                        if (feedback != null) {
                            ExerciseWork exerciseWork = ExerciseWorkDAO.getInstance().getWork(studentId, exerciseDto1.getId());
                            exerciseDto1.setStudentWork(exerciseWork.getWork());
                            sendMessage(STR."\{JSONUtil.stringify(exerciseDto1)}-\{JSONUtil.stringify(feedback)}");
                            break;
                        }
                        sendMessage(JSONUtil.stringify(exerciseDto1));
                        break;
                    case "GET_SPEAK_WORK":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        studentId = Integer.parseInt(splitMessage[2]);
                        audioFile = new File(STR."./src/main/resources/audios/speaking-ex/\{lessonId}\{studentId}.wav");
                        output.write(STR."START_SEND_FILE-audios-\{audioFile.getName()}-\{String.valueOf(audioFile.length())}".getBytes());
                        buffer3 = new byte[4096];

                        int count3;
                        InputStream in3 = new FileInputStream(audioFile);
                        while ((count3 = in3.read(buffer3)) > 0) {
                            output.write(buffer3, 0, count3);
                        }
                        break;
                    case "SUBMIT_REWRITE_EX":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        studentId = Integer.parseInt(splitMessage[2]);
                        exerciseDtoList = JSONUtil.parseList(splitMessage[3], ExerciseDto.class);
                        ExerciseWorkDAO exerciseWorkDAO = ExerciseWorkDAO.getInstance();
                        FeedbackDAO feedbackDAO = FeedbackDAO.getInstance();
                        feedbackDAO.initFeedback(studentId, lessonId, "rewrite");
                        for (ExerciseDto exerciseDto: exerciseDtoList) {
                            exerciseWorkDAO.save(exerciseDto, studentId);
                        }
                        break;
                    case "SUBMIT_ESSAY_EX":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        studentId = Integer.parseInt(splitMessage[2]);
                        ExerciseDto exerciseDto = JSONUtil.parse(splitMessage[3], ExerciseDto.class);
                        FeedbackDAO.getInstance().initFeedback(studentId, lessonId, "essay");
                        ExerciseWorkDAO.getInstance().save(exerciseDto, studentId);
                        break;
                    case "SUBMIT_SPEAK_EX":
                        lessonId = Integer.parseInt(splitMessage[1]);
                        studentId = Integer.parseInt(splitMessage[2]);
                        ExerciseDto exerciseDto2 = JSONUtil.parse(splitMessage[3], ExerciseDto.class);
                        FeedbackDAO.getInstance().initFeedback(studentId, lessonId, "speak");
                        ExerciseWorkDAO.getInstance().save(exerciseDto2, studentId);
                        FileOutputStream fos = new FileOutputStream(STR."./src/main/resources/audios/speaking-ex/\{lessonId}\{studentId}.wav");
                        byte[] fileBuffer = new byte[4096];
                        while ((count = input.read(fileBuffer)) != -1) {
                            fos.write(fileBuffer, 0, count);
                        }
                        fos.close();
                        break;
                    case "GET_SUBMISSION_LIST":
                        String exType = splitMessage[1];
                        List<Submission> submissionList = ExerciseWorkDAO.getInstance().getSubmissionList(exType);
                        sendMessage(JSONUtil.stringify(submissionList));
                        break;
                    case "GIVE_FEEDBACK":
                        Feedback feedback1 = JSONUtil.parse(splitMessage[1], Feedback.class);
                        FeedbackDAO.getInstance().giveFeedback(feedback1);
                        break;
                    case "GET_LEVEL_LIST":
                        String gameType = splitMessage[1];
                        File folder = new File(STR."./src/main/resources/images/\{gameType}");
                        sendMessage(String.valueOf(Objects.requireNonNull(folder.listFiles()).length));
                        break;
                    case "GET_WORD_GAME_IMAGE":
                        int level = Integer.parseInt(splitMessage[1]);
                        int imageIndex = Integer.parseInt(splitMessage[2]);
                        folder = new File(STR."./src/main/resources/images/word/l\{level}");
                        File[] files = folder.listFiles();
                        output.write(STR."START_SEND_FILE-images/word/l\{level}-\{files[imageIndex].getName()}-\{String.valueOf(files[imageIndex].length())}".getBytes());
                        byte[] buffer4 = new byte[4096];

                        int count4;
                        InputStream in4 = new FileInputStream(files[imageIndex]);
                        while ((count4 = in4.read(buffer4)) > 0) {
                            output.write(buffer4, 0, count4);
                        }
                        break;
                    case "GET_ONLINE_USERS":
                        Set<String> keySet = Main.onlineUsers.keySet();

                        // Convert the key set to an array
                        String[] onlineUsers = new String[keySet.size()];
                        keySet.toArray(onlineUsers);
                        sendMessage(JSONUtil.stringify(onlineUsers));
                        break;
                    case "GET_USERS":
                        List<User> userList = UserDAO.getInstance().getUsers();
                        sendMessage(JSONUtil.stringify(userList));
                        break;
                    case "SIGN_OUT":
                        Main.onlineUsers.remove(this.user.getLoginId());
                        break;
                    case "GET_MESSAGE_LIST":
                        int userId = Integer.parseInt(splitMessage[1]);
                        List<Message> messageList = MessageDAO.getInstance().getMessageList(userId, this.user.getId());
                        sendMessage(JSONUtil.stringify(messageList));
                        break;
                    case "SEND_MESSAGE":
                        User toUSer = JSONUtil.parse(splitMessage[1], User.class);
                        String content = splitMessage[2];
                        MessageDAO.getInstance().insertMessage(this.user.getId(), toUSer.getId(), content);
                        if (Main.onlineUsers.containsKey(toUSer.getLoginId())) {
                            Main.onlineUsers.get(toUSer.getLoginId()).sendMessage(STR."RECEIVE_MESSAGE-\{JSONUtil.stringify(this.user)}-\{content}");
                        }
                        break;
                }
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) throws IOException {
        output.write(message.getBytes());
        logger.info(message);
    }
}
