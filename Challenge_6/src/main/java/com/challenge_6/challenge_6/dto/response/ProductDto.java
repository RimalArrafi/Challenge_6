package com.challenge_6.challenge_6.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDto {
    private UUID id;

    @NotNull
    private MerchantDto merchant;

    @NotEmpty
    private String productName;

    @NotNull
    private Double price;
}
