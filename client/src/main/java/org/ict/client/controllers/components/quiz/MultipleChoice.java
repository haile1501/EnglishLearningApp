package org.ict.client.controllers.components.quiz;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import org.ict.client.models.QuizQuestion;

public class MultipleChoice {
    @FXML
    private Text question;
    @FXML
    private RadioButton a;
    @FXML
    private RadioButton b;
    @FXML
    private RadioButton c;
    @FXML
    private RadioButton d;

    public void setData(QuizQuestion quizQuestion) {
        this.question.setText(quizQuestion.getQuestion());
        this.a.setText(quizQuestion.getA());
        this.b.setText(quizQuestion.getB());
        this.c.setText(quizQuestion.getC());
        this.d.setText(quizQuestion.getD());

        ToggleGroup tg = new ToggleGroup();
        a.setToggleGroup(tg);
        b.setToggleGroup(tg);
        c.setToggleGroup(tg);
        d.setToggleGroup(tg);

        tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            public void changed(ObservableValue<? extends Toggle> ob,
                                Toggle o, Toggle n)
            {

                RadioButton rb = (RadioButton)tg.getSelectedToggle();

                if (rb != null) {
                    String s = rb.getText();
                    quizQuestion.setAnswer(s);
                }
            }
        });
    }
}
