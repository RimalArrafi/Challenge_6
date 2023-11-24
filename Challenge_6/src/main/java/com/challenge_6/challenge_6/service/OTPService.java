package com.challenge_6.challenge_6.service;

import com.challenge_6.challenge_6.entity.OTP;
import com.challenge_6.challenge_6.exception.ApiException;

public interface OTPService {
    public OTP generateOTP();

    public void otpIsValid(Integer otpCode) throws ApiException;
}
