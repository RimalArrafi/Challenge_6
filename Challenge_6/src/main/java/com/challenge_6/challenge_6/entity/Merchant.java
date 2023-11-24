package com.challenge_6.challenge_6.entity;

import java.util.UUID;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "merchants")
@Data
@NoArgsConstructor
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_name")
    @NotNull
    private String merchantName;

    @Column(name = "merchant_location")
    @NotNull
    private String merchantLocation;

    private Boolean isOpen = false;
    
    @OneToOne(mappedBy = "merchant")
    private User user;
}
