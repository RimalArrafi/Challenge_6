package com.challenge_6.challenge_6.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;

import com.challenge_6.challenge_6.dto.OrderDto;
import com.challenge_6.challenge_6.dto.request.CreateOrderDto;
import com.challenge_6.challenge_6.exception.ApiException;

public interface OrderService {
    public OrderDto createOrder(CreateOrderDto request) throws ApiException;

    public List<OrderDto> getAllOrders();

    public List<OrderDto> getOrdersByUserId(UUID userId, UserDetails userDetails) throws ApiException;

    public OrderDto getOrderById(UUID orderId, UserDetails userDetails) throws ApiException;

    public OrderDto deleteOrder(UUID orderId) throws ApiException;
}
