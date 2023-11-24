package com.challenge_6.challenge_6.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.challenge_6.challenge_6.dto.request.CreateUserDto;
import com.challenge_6.challenge_6.dto.request.UpdateUserDto;
import com.challenge_6.challenge_6.dto.response.UserDto;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.repository.UserRepository;
import com.challenge_6.challenge_6.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(CreateUserDto request) {
        User user = modelMapper.map(request, User.class);
        UserDto userDto = modelMapper.map(userRepository.save(user), UserDto.class);
        return userDto;
    }

    @Override
    public UserDto updateUser(UUID userId, UpdateUserDto request, UserDetails userDetails) throws ApiException {
        Optional<User> userOnDb = userRepository.findById(userId);

        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "User with id " + userId + " is not found!");

        if (!userOnDb.get().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't retrieve other user's data");

        User existedUser = userOnDb.get();
        if (request.getUsername().isPresent())
            existedUser.setUsername(request.getUsername().get());
        if (request.getEmailAddress().isPresent())
            existedUser.setEmailAddress(request.getEmailAddress().get());
        if (request.getPassword().isPresent())
            existedUser.setPassword(passwordEncoder.encode(request.getPassword().get()));
        UserDto userDto = modelMapper.map(userRepository.save(existedUser), UserDto.class);

        return userDto;
    }

    @Override
    public UserDto getUserById(UUID userId, UserDetails userDetails) throws ApiException {
        Optional<User> userOnDb = userRepository.findById(userId);
        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "User with id " + userId + " is not found!");

        if (!userOnDb.get().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't retrieve other user's data");

        UserDto userDto = modelMapper.map(userOnDb.get(), UserDto.class);
        return userDto;
    }

    @Override
    public List<UserDto> getAllUsers(Specification<User> filterQueries) {
        List<User> users = userRepository.findAll(filterQueries);
        List<UserDto> usersDto = users.stream().map(m -> modelMapper.map(m, UserDto.class))
                .collect(Collectors.toList());
        return usersDto;
    }

    @Override
    public UserDto deleteUser(UUID userId, UserDetails userDetails) throws ApiException {
        Optional<User> userOnDb = userRepository.findById(userId);
        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "User with id " + userId + " is not found!");

        if (!userOnDb.get().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't delete other user's data");

        User deletedUser = userOnDb.get();
        userRepository.delete(deletedUser);
        UserDto userDto = modelMapper.map(deletedUser, UserDto.class);
        return userDto;
    }

}
