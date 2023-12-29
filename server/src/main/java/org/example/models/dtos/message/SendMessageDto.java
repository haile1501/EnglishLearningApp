package org.example.models.dtos.message;

import lombok.Getter;

@Getter
public class SendMessageDto {
    private Long senderId;
    private Long receiverId;
    private String content;
}
