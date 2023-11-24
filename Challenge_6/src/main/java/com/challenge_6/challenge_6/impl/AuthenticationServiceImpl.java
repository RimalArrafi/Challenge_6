package com.challenge_6.challenge_6.impl;

import java.util.Arrays;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.challenge_6.challenge_6.dto.request.EnableUserDto;
import com.challenge_6.challenge_6.dto.request.ForgotPasswordDto;
import com.challenge_6.challenge_6.dto.request.LoginUserDto;
import com.challenge_6.challenge_6.dto.request.RegisterUserDto;
import com.challenge_6.challenge_6.dto.response.AuthenticationDto;
import com.challenge_6.challenge_6.dto.response.UserDto;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.repository.RoleRepository;
import com.challenge_6.challenge_6.repository.UserRepository;
import com.challenge_6.challenge_6.service.AuthenticationService;
import com.challenge_6.challenge_6.service.JWTService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public User register(RegisterUserDto request) throws ApiException {
        Optional<User> userOnDb = userRepository.findByUsername(request.getUsername());
        boolean emailAlreadyExist = userRepository.existsByEmailAddress(request.getEmailAddress());

        if (userOnDb.isPresent())
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Username '" + request.getUsername() + "' is already exist!");

        if (emailAlreadyExist)
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Email '" + request.getEmailAddress() + "' is already exist!");

        User user = modelMapper.map(request, User.class);
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_CUSTOMER").get()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        return userRepository.save(user);
    }

    @Override
    public AuthenticationDto login(LoginUserDto request) throws ApiException {
        Optional<User> userOnDb = userRepository.findByUsername(request.getUsername());

        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "User with username '" + request.getUsername() + "' doesn't exist");

        if (!userOnDb.get().isEnabled())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "Your account hasn't activated yet");

        try {
            User user = userOnDb.get();
            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()));

            String jwtToken = jwtService.generateToken(user);
            AuthenticationDto authDto = new AuthenticationDto();
            authDto.setUserData(modelMapper.map(user, UserDto.class));
            authDto.setAccessToken(jwtToken);
            return authDto;
        } catch (AuthenticationException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Password doesn't match!");
        }
    }

    @Override
    public AuthenticationDto enableUser(EnableUserDto request) throws ApiException {
        Optional<User> userOnDb = userRepository.findByEmailAddress(request.getEmailAddress());

        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "User with username '" + request.getEmailAddress() + "' doesn't exist");

        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "User with username '" + request.getEmailAddress() + "' doesn't exist");

        if (userOnDb.get().isEnabled())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "User with username '" + request.getEmailAddress() + "' already enabled");

        User user = userOnDb.get();
        user.setEnabled(true);
        userRepository.save(user);

        AuthenticationDto authDto = new AuthenticationDto();
        authDto.setUserData(modelMapper.map(user, UserDto.class));
        authDto.setAccessToken(jwtService.generateToken(user));
        return authDto;
    }

    @Override
    public User updateUserPassword(ForgotPasswordDto request) throws ApiException {
        Optional<User> userOnDb = userRepository.findByEmailAddress(request.getEmailAddress());

        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "User with email '" + request.getEmailAddress() + "' doesn't exist");

        User user = userOnDb.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

}
