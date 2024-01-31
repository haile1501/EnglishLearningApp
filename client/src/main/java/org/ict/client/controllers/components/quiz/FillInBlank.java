package org.ict.client.controllers.components.quiz;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.ict.client.models.QuizQuestion;

public class FillInBlank {
    @FXML
    private Text question;
    @FXML
    private TextField answer;

    public void setData(QuizQuestion quizQuestion) {
        question.setText(quizQuestion.getQuestion());

        answer.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                quizQuestion.setAnswer(newValue);
            }
        });
    }
}
