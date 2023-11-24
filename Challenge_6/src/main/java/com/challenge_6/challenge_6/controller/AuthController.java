package com.challenge_6.challenge_6.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge_6.challenge_6.dto.request.EnableUserDto;
import com.challenge_6.challenge_6.dto.request.ForgotPasswordDto;
import com.challenge_6.challenge_6.dto.request.ForgotPasswordSendOtpDto;
import com.challenge_6.challenge_6.dto.request.LoginUserDto;
import com.challenge_6.challenge_6.dto.request.RegisterUserDto;
import com.challenge_6.challenge_6.dto.response.AuthenticationDto;
import com.challenge_6.challenge_6.entity.OTP;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.service.AuthenticationService;
import com.challenge_6.challenge_6.service.OTPService;
import com.challenge_6.challenge_6.utils.EmailSender;
import com.challenge_6.challenge_6.utils.EmailTemplate;
import com.challenge_6.challenge_6.utils.ResponseHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    OTPService otpService;

    @Autowired
    EmailSender emailSender;

    @Autowired
    EmailTemplate emailTemplate;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterUserDto request) {
        try {
            User userData = authenticationService.register(request);

            OTP otp = otpService.generateOTP();

            String template = emailTemplate.getRegisterTemplate(userData.getUsername(),
                    otp.getOneTimePasswordCode());

            emailSender.sendEmail(userData.getEmailAddress(), "Register new account", template);

            Map<String, String> res = new HashMap<String, String>();
            res.put("message", "Please check your email and enter the OTP!");

            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Register Success", res);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginUserDto request) {
        try {
            AuthenticationDto userData = authenticationService.login(request);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Login Success", userData);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/enable-user")
    public ResponseEntity<Object> enableUser(@Valid @RequestBody EnableUserDto request) {
        try {
            otpService.otpIsValid(request.getOtpCode());

            AuthenticationDto userData = authenticationService.enableUser(request);

            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Register Success", userData);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<Object> forgotPasswordSendOtp(@Valid @RequestBody ForgotPasswordSendOtpDto request) {
        try {
            OTP otp = otpService.generateOTP();

            String template = emailTemplate.getResetPassword(request.getEmailAddress(),
                    otp.getOneTimePasswordCode());

            emailSender.sendEmail(request.getEmailAddress(), "Forgot password", template);

            Map<String, String> res = new HashMap<String, String>();
            res.put("message", "Please check your email and enter the OTP!");

            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Forgot password", res);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordDto request) {
        try {
            otpService.otpIsValid(request.getOtpCode());
            authenticationService.updateUserPassword(request);

            Map<String, String> res = new HashMap<String, String>();
            res.put("message", "Please login again!");

            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Register Success", res);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
