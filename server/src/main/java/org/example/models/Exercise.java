package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exercise extends BaseModel{
    public enum EX_TYPE{REWRITE, PARAGRAPH, SPEAKING};
//    Integer exerciseId;
    String content;
    EX_TYPE type;
    Integer lessonId;
}
