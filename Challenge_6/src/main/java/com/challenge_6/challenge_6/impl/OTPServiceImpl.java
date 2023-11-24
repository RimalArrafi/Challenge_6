package com.challenge_6.challenge_6.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.challenge_6.challenge_6.entity.OTP;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.repository.OTPRepository;
import com.challenge_6.challenge_6.service.OTPService;

@Service
public class OTPServiceImpl implements OTPService {
    private final Long expiryInterval = 5L * 60 * 1000;

    private final int otpLength = 6;

    @Autowired
    OTPRepository otpRepository;

    public Integer createRandomOneTimePassword() {
        Random random = new Random();
        StringBuilder oneTimePassword = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            int randomNumber = random.nextInt(10);
            oneTimePassword.append(randomNumber);
        }
        return Integer.parseInt(oneTimePassword.toString().trim());

    }

    @Override
    public OTP generateOTP() {
        OTP otp = new OTP();
        otp.setOneTimePasswordCode(createRandomOneTimePassword());
        otp.setExpires(new Date(System.currentTimeMillis() + expiryInterval));
        return otpRepository.save(otp);
    }

    @Override
    public void otpIsValid(Integer otpCode) throws ApiException {
        List<OTP> otpList = otpRepository.findAllByOneTimePasswordCode(otpCode);
        if (otpList.size() == 0)
            throw new ApiException(HttpStatus.NOT_FOUND, "OTP " + otpCode + " is not found!");

        OTP otp = otpList.stream().filter(o -> o.getOneTimePasswordCode().equals(otpCode) && !o.getIsUsed())
                .findAny().orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Token is invalid or already expired"));

        if (otp.getExpires().before(new Date()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "OTP " + otpCode + " is expired!");

        Optional<OTP> updateOtpOnDb = otpRepository.findById(otp.getId());
        OTP updateOtp = updateOtpOnDb.get();
        updateOtp.setIsUsed(true);
        otpRepository.save(updateOtp);
    }

}
