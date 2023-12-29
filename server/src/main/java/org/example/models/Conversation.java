package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Conversation extends BaseModel {
    private Long firstUserId;
    private Long secondUserId;
}
