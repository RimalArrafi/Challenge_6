package com.challenge_6.challenge_6.service;

import com.challenge_6.challenge_6.dto.request.EnableUserDto;
import com.challenge_6.challenge_6.dto.request.ForgotPasswordDto;
import com.challenge_6.challenge_6.dto.request.LoginUserDto;
import com.challenge_6.challenge_6.dto.request.RegisterUserDto;
import com.challenge_6.challenge_6.dto.response.AuthenticationDto;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;

public interface AuthenticationService {
    public User register(RegisterUserDto request) throws ApiException;

    public AuthenticationDto login(LoginUserDto request) throws ApiException;

    public AuthenticationDto enableUser(EnableUserDto request) throws ApiException;

    public User updateUserPassword(ForgotPasswordDto request) throws ApiException;
}
