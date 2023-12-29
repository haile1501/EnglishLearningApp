package org.example.models.dtos.conversation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationSummary {
    private Long participantId;
    private String participantName;
}
