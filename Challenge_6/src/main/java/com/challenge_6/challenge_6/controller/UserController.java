package com.challenge_6.challenge_6.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge_6.challenge_6.dto.request.UpdateUserDto;
import com.challenge_6.challenge_6.dto.response.UserDto;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.repository.UserRepository;
import com.challenge_6.challenge_6.service.UserService;
import com.challenge_6.challenge_6.utils.ResponseHandler;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId,
            @Valid @RequestBody UpdateUserDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDto editedUser = userService.updateUser(userId, request, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "User has successfully edited!", editedUser);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDto user = userService.getUserById(userId, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "User has successfully fetched!", user);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String emailAddress) {
        try {
            Specification<User> filterQueries = ((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (username != null && !username.isEmpty()) {
                    predicates.add(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" +
                                    username.toLowerCase() + "%"));
                }
                if (emailAddress != null && !emailAddress.isEmpty()) {
                    predicates.add(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("emailAddress")), "%"
                                    +
                                    emailAddress.toLowerCase() + "%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            List<UserDto> users = userService.getAllUsers(filterQueries);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "User has successfully fetched!", users);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDto deletedUser = userService.deleteUser(userId, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "User has successfully deleted!", deletedUser);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
