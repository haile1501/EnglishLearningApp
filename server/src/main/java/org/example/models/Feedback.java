package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feedback extends BaseModel {
    private String type;
    private String status;
    private int score;
    private String comment;
    private Integer lessonId;
    private Integer studentId;
    private Integer teacherId;
}
