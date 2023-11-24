package com.challenge_6.challenge_6.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;

import com.challenge_6.challenge_6.dto.request.CreateUserDto;
import com.challenge_6.challenge_6.dto.request.UpdateUserDto;
import com.challenge_6.challenge_6.dto.response.UserDto;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;

public interface UserService {
    public UserDto createUser(CreateUserDto request);

    public UserDto updateUser(UUID userId, UpdateUserDto request, UserDetails userDetails) throws ApiException;

    public UserDto getUserById(UUID userId, UserDetails userDetails) throws ApiException;

    public UserDto deleteUser(UUID userId, UserDetails userDetails) throws ApiException;;

    public List<UserDto> getAllUsers(Specification<User> filterQueries);
}
