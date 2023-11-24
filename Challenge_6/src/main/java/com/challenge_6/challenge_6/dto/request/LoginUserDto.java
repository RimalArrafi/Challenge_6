package com.challenge_6.challenge_6.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
