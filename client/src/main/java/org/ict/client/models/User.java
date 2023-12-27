package org.ict.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseModel {
    private String loginId;
    private String password;
    private String role;
}
