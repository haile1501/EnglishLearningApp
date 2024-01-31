package org.example.models.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.models.BaseModel;

@Getter
@Setter
public class Submission {

    private String lesson;
    private String student;
    private String type;
    private int lessonId;
    private int studentId;
}
