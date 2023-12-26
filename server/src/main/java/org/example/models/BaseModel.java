package org.example.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public abstract class BaseModel {
    private Integer id;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
