package org.ict.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionListItem {

    private String lesson;
    private String student;
    private String type;
    private int lessonId;
    private int studentId;
}
