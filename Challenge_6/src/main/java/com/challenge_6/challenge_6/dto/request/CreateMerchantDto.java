package com.challenge_6.challenge_6.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMerchantDto {
    @NotBlank()
    private String merchantName;

    @NotBlank()
    private String merchantLocation;

    private Boolean isOpen = false;
}
