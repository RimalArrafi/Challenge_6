package com.challenge_6.challenge_6.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.challenge_6.challenge_6.dto.request.CreateProductDto;
import com.challenge_6.challenge_6.dto.request.UpdateProductDto;
import com.challenge_6.challenge_6.dto.response.ProductDto;
import com.challenge_6.challenge_6.entity.Product;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.service.ProductService;
import com.challenge_6.challenge_6.utils.ResponseHandler;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;

@Controller

@RequestMapping("/v1/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping
    public ResponseEntity<Object> createProduct(@Valid @RequestBody CreateProductDto request) {
        try {
            ProductDto newProduct = productService.createProduct(request);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK, "Product has successfully created!",
                    newProduct);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Object> updateProduct(@PathVariable UUID productId,
            @Valid @RequestBody UpdateProductDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ProductDto editedProduct = productService.updateProduct(productId, request, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK, "Product has successfully eited!",
                    editedProduct);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Object> getMerchantById(@PathVariable UUID productId) {
        try {
            ProductDto product = productService.getProductById(productId);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Product has successfully fetched!", product);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (page == null)
                page = 0;
            if (pageSize == null)
                pageSize = 10;

            Pageable paginationQueries = PageRequest.of(page, pageSize);
            Specification<Product> filterQueries = ((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (productName != null && !productName.isEmpty()) {
                    predicates.add(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" +
                                    productName.toLowerCase() + "%"));
                }
                if (merchantId != null) {
                    predicates.add(
                            criteriaBuilder.equal(root.get("merchant").get("id"), merchantId));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            Page<ProductDto> products = productService.getAllProducts(filterQueries, paginationQueries);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Products has successfully fetched!", products);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable UUID productId) {
        try {
            ProductDto deletedProduct = productService.deleteProduct(productId);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Product has successfully deleted!", deletedProduct);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
