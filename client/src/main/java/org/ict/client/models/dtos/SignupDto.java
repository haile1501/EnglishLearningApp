package org.ict.client.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignupDto {
    private String loginId;
    private String password;
    private String role;
}
