package com.challenge_6.challenge_6.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderDetailDto {
    @NotNull
    private UUID productId;

    @NotNull
    private String productName;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private Double totalPrice;

    @NotNull
    private Double price;
}
