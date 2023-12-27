package org.ict.client.controllers.studentpages;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.ict.client.SocketManager;
import org.ict.client.models.dtos.GetLessonListDto;
import org.ict.client.utils.JSONUtil;

import java.io.IOException;

public class LessonList {
//    @FXML
//    private GridPane lessonList;
//
//    @FXML
//    private TextField topic;
//
//    @FXML
//    private ChoiceBox<String> level;

    @FXML
    protected void initialize() throws IOException {
//        topic.setText("");
//        level.setItems(
//                FXCollections.observableArrayList("Beginner", "Intermediate", "Advance"));
//        level.setValue("Beginner");
        SocketManager socketManager = SocketManager.getInstance();
        GetLessonListDto getLessonListDto = new GetLessonListDto("", "Beginner");
        socketManager.sendMessage("GET_LESSON_LIST " + JSONUtil.stringify(getLessonListDto), this::handleLessonsResponse);
    }

    private void handleLessonsResponse(String message) {
        System.out.println(message);
    }
}
