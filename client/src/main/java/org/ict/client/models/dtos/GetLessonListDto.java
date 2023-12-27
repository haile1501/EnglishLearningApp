package org.ict.client.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetLessonListDto {

    private String topic;
    private String level;
}
