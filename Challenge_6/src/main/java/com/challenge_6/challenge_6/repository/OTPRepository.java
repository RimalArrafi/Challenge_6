package com.challenge_6.challenge_6.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge_6.challenge_6.entity.OTP;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    List<OTP> findAllByOneTimePasswordCode(Integer otpCode);
}
