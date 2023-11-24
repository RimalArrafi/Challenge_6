package com.challenge_6.challenge_6.dto.request;

import java.util.Optional;

import lombok.Data;

@Data
public class UpdateUserDto {
    private Optional<String> username;

    private Optional<String> emailAddress;

    private Optional<String> password;

}
