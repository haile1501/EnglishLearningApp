package org.ict.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.ict.client.SocketManager;
import org.ict.client.models.Exercise;
import org.ict.client.models.dtos.SubmitDto;
import org.ict.client.utils.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExerciseView {
    private SocketManager socketManager;
    @FXML
    private SplitPane pane;

    @FXML
    private TextArea exDetail;

    @FXML
    private TextArea exWork;

    @FXML
    private ListView exList;

    private Integer curEx;
    private List<Exercise> _exList;

    @FXML
    private void initialize() throws ClassNotFoundException, IOException {
        socketManager = SocketManager.getInstance();
        socketManager.sendMessage("GET_EXERCISE_LIST {\"accountId\":\"abc\"}", this::handleDataResponse);
        exList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String idStr = exList.getSelectionModel().getSelectedItem().toString().split(" ")[0];
                Integer id = Integer.parseInt(idStr.substring(0, idStr.length() - 1));
                curEx = id;
                exDetail.setText(_exList.get(id - 1).getContent());
            }
        });
        exWork.setText("Work");
        exDetail.setEditable(false);
    }

    public void handleSubmit() throws IOException {
        System.out.println("Submit");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Submit confirmation");
        alert.setHeaderText("Do you want to submit?");
        alert.setContentText("You can't undo if you submit");
        alert.showAndWait();
        String workContent = exWork.getText();
        socketManager = SocketManager.getInstance();
        Object payload = new SubmitDto(1, curEx,1,2, workContent);
        socketManager.sendMessage("SUBMIT_EXERCISE " + JSONUtil.stringify(payload), this::handleSubmitDataResponse);

    }

    @FXML
    public void handleMouseClick(MouseEvent arg0) {
        System.out.println(exList.getSelectionModel().getSelectedItem());
    }

    private void handleDataResponse(String data) throws JsonProcessingException {
        Platform.runLater(() -> {
            try {
                _exList = (List<Exercise>) JSONUtil.parseList(data, Exercise.class);
                ObservableList<String> names = FXCollections.observableArrayList();
                curEx = _exList.get(0).getId();
                for (Exercise ex : _exList) {
                    names.add(ex.getId() + ". Lesson ID: " + ex.getLessonId() + ": " + ex.getType().toString());
                }
                exList.setItems(names);
                exDetail.setText(_exList.get(0).getContent());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleSubmitDataResponse(String data) throws JsonProcessingException {
        System.out.println(data);
    }

}
