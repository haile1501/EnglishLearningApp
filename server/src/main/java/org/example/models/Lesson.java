package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lesson extends BaseModel {

    private String topic;
    private String level;
    private String audioUrl;
    private String videoUrl;
}
