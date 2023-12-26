package org.example.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Role extends BaseModel {
    private String name;
    private String type;
}
