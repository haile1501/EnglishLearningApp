package org.ict.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message extends BaseModel {

    private String content;
    private int senderId;
    private int receiverId;
}
