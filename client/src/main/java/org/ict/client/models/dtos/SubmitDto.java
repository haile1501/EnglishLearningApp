package org.ict.client.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubmitDto {
    Integer id;
    Integer exerciseId;
    Integer studentId;
    Integer teacherId;
    String work;
}
