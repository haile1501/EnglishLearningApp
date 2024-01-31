package org.ict.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exercise extends BaseModel{
    private String type;
    private String content;
    private String studentWork;
}
