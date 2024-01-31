package org.example.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.models.BaseModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionDto extends BaseModel {
    private String type;
    private String question;
    private String a;
    private String b;
    private String c;
    private String d;
    private String answer = "";
}
