package com.challenge_6.challenge_6.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge_6.challenge_6.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

}
