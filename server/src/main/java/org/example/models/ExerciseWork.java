package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseWork extends BaseModel{
    Integer exerciseId;
    Integer studentId;
    String work;
}
