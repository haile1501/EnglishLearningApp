package org.ict.client.controllers.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.ict.client.models.Exercise;
import org.ict.client.models.QuizQuestion;

public class SentenceRewrite {
    @FXML
    private Text question;
    @FXML
    private TextField answer;

    public void setData(Exercise exercise, boolean isSubmitted) {
        question.setText(exercise.getContent());
        if (isSubmitted) {
            answer.setText(exercise.getStudentWork());
            answer.setEditable(false);
        }

        answer.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                exercise.setStudentWork(newValue);
            }
        });
    }
}
