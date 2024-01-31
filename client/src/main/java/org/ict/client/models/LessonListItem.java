package org.ict.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonListItem extends BaseModel {
    private String topic;
    private String level;
    private String audioUrl;
    private String videoUrl;
}
