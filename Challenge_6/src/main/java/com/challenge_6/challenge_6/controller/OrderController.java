package com.challenge_6.challenge_6.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.challenge_6.challenge_6.dto.OrderDto;
import com.challenge_6.challenge_6.dto.request.CreateOrderDto;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.service.OrderService;
import com.challenge_6.challenge_6.utils.ResponseHandler;

import jakarta.validation.Valid;

@Controller

@RequestMapping("/v1/orders")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderDto request) {
        try {
            OrderDto newOrder = orderService.createOrder(request);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Order has successfully created!", newOrder);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Object> deleteOrder(@PathVariable UUID orderId) {
        try {
            OrderDto order = orderService.deleteOrder(orderId);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Order has successfully deleted!", order);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllOrders() {
        try {
            List<OrderDto> orders = orderService.getAllOrders();
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Orders has successfully fetched!", orders);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            OrderDto order = orderService.getOrderById(orderId, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Order has successfully fetched!", order);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("users/{userId}")
    public ResponseEntity<Object> getOrdersByUserId(@PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<OrderDto> userOrders = orderService.getOrdersByUserId(userId, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "User orders has successfully fetched!", userOrders);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
