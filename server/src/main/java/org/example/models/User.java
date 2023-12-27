package org.example.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseModel {
    private String loginId;
    private String password;
    private String role;
}
