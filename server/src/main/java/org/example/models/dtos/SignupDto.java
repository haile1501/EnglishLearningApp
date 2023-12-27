package org.example.models.dtos;

import lombok.Getter;

@Getter
public class SignupDto {
    private String loginId;
    private String password;
    private String role;
}
