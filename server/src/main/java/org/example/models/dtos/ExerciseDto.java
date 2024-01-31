package org.example.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.models.BaseModel;
import org.example.models.Exercise;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDto extends BaseModel {
    private String studentWork = "";
    private String content;
    private String type;
}
