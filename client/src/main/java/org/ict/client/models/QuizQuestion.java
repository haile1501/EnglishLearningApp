package org.ict.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizQuestion extends BaseModel {

    private String type;
    private String question;
    private String a;
    private String b;
    private String c;
    private String d;
    private String answer;
}
