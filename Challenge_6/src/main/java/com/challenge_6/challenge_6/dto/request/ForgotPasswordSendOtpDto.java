package com.challenge_6.challenge_6.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordSendOtpDto {
    @NotBlank
    @Email
    private String emailAddress;
}
