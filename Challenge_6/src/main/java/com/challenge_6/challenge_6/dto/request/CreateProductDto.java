package com.challenge_6.challenge_6.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProductDto {
    @NotNull
    private UUID merchantId;

    @NotBlank
    @Size(min = 3, message = "must has min 3 characters")
    private String productName;

    @NotNull
    @Positive
    private Double price;
}
