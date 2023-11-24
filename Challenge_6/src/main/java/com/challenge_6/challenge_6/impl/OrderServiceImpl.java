package com.challenge_6.challenge_6.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.challenge_6.challenge_6.dto.OrderDto;
import com.challenge_6.challenge_6.dto.request.CreateOrderDto;
import com.challenge_6.challenge_6.dto.response.OrderDetailDto;
import com.challenge_6.challenge_6.entity.Order;
import com.challenge_6.challenge_6.entity.OrderDetail;
import com.challenge_6.challenge_6.entity.Product;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.repository.OrderDetailRepository;
import com.challenge_6.challenge_6.repository.OrderRepository;
import com.challenge_6.challenge_6.repository.ProductRepository;
import com.challenge_6.challenge_6.repository.UserRepository;
import com.challenge_6.challenge_6.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDto createOrder(CreateOrderDto request) throws ApiException {
        Optional<User> userOnDb = userRepository.findById(request.getUserId());

        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "User with id " + request.getUserId() + " is not found!");

        Order order = new Order();
        order.setDestinationAddress(request.getDestinationAddress());
        order.setUser(userOnDb.get());
        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        Double finalPrice = 0.0;
        for (OrderDetailDto od : request.getOrderDetails()) {
            OrderDetail orderDetail = new OrderDetail();
            Optional<Product> product = productRepository.findById(od.getProductId());
            if (product.isPresent()) {
                orderDetail.setOrder(savedOrder);
                orderDetail.setProduct(product.get());
                orderDetail.setQuantity(od.getQuantity());
                orderDetail.setTotalPrice(product.get().getPrice() * od.getQuantity());
                orderDetails.add(orderDetail);

                finalPrice += product.get().getPrice() * od.getQuantity();
            }
        }
        savedOrder.setOrderDetails(orderDetailRepository.saveAll(orderDetails));

        OrderDto orderDto = modelMapper.map(savedOrder, OrderDto.class);
        orderDto.setFinalPrice(finalPrice);
        return orderDto;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<OrderDto> ordersDto = orderRepository.findAll().stream().map(o -> {
            OrderDto orderDto = modelMapper.map(o, OrderDto.class);
            Double finalPrice = o.getOrderDetails().stream().mapToDouble(od -> od.getTotalPrice()).sum();
            orderDto.setFinalPrice(finalPrice);

            return orderDto;
        }).collect(Collectors.toList());

        return ordersDto;
    }

    @Override
    public List<OrderDto> getOrdersByUserId(UUID userId, UserDetails userDetails) throws ApiException {
        Optional<User> userOnDb = userRepository.findById(userId);
        if (userOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "User with id " + userId + " is not found!");

        if (!userOnDb.get().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't retrieve other user's order");

        List<Order> orders = orderRepository.findAllByUserId(userId);
        List<OrderDto> ordersDto = orders.stream().map(o -> {
            OrderDto orderDto = modelMapper.map(o, OrderDto.class);
            Double finalPrice = o.getOrderDetails().stream().mapToDouble(od -> od.getTotalPrice()).sum();
            orderDto.setFinalPrice(finalPrice);

            return orderDto;
        }).collect(Collectors.toList());

        return ordersDto;
    }

    @Override
    public OrderDto getOrderById(UUID orderId, UserDetails userDetails) throws ApiException {
        Optional<Order> orderOnDb = orderRepository.findById(orderId);
        if (orderOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "Order with id " + orderId + " is not found!");

        if (!orderOnDb.get().getUser().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't retrieve other user's order");

        OrderDto orderDto = modelMapper.map(orderOnDb.get(), OrderDto.class);
        Double finalPrice = orderOnDb.get().getOrderDetails().stream().mapToDouble(od -> od.getTotalPrice()).sum();
        orderDto.setFinalPrice(finalPrice);

        return orderDto;
    }

    @Override
    public OrderDto deleteOrder(UUID orderId) throws ApiException {
        Optional<Order> orderOnDb = orderRepository.findById(orderId);
        if (orderOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "Order with id " + orderId + " is not found!");

        Order deletedOrder = orderOnDb.get();
        orderRepository.delete(deletedOrder);

        OrderDto orderDto = modelMapper.map(deletedOrder, OrderDto.class);
        Double finalPrice = orderOnDb.get().getOrderDetails().stream().mapToDouble(od -> od.getTotalPrice()).sum();
        orderDto.setFinalPrice(finalPrice);

        return orderDto;
    }

}
