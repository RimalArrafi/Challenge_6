package com.challenge_6.challenge_6.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
class User {
    private UUID id;
    private String username;
    private String emailAddress;
}

@Data
public class MerchantDto {
    private UUID id;

    @NotEmpty()
    private String merchantName;

    @NotEmpty()
    private String merchantLocation;

    private Boolean isOpen;

    private User user;
}
