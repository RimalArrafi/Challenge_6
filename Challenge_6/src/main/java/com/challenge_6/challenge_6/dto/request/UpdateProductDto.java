package com.challenge_6.challenge_6.dto.request;

import java.util.Optional;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateProductDto {
    private Optional<UUID> merchantId;

    private Optional<String> productName;

    private Optional<Double> price;
}
