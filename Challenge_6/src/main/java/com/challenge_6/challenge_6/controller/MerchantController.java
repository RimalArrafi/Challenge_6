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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge_6.challenge_6.dto.request.CreateMerchantDto;
import com.challenge_6.challenge_6.dto.request.UpdateMerchantDto;
import com.challenge_6.challenge_6.dto.response.MerchantDto;
import com.challenge_6.challenge_6.entity.Merchant;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.service.MerchantService;
import com.challenge_6.challenge_6.utils.ResponseHandler;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/merchants")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    @PostMapping
    public ResponseEntity<Object> createMerchant(@Valid @RequestBody CreateMerchantDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MerchantDto newMerchant = merchantService.createMerchant(request, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Merchant has successfully created!", newMerchant);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMerchant(@PathVariable UUID id,
            @Valid @RequestBody UpdateMerchantDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MerchantDto editedMerchant = merchantService.updateMerchant(id,
                    request, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Merchant has successfully edited!", editedMerchant);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<Object> getAllMerchants(
            @RequestParam(required = false) Boolean isOpen,
            @RequestParam(required = false) String merchantName,
            @RequestParam(required = false) String merchantLocation) {
        try {
            Specification<Merchant> filterQueries = ((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (isOpen != null) {
                    predicates.add(
                            criteriaBuilder.equal(root.get("isOpen"), isOpen));
                }
                if (merchantName != null && !merchantName.isEmpty()) {
                    predicates.add(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("merchantName")), "%" +
                                    merchantName.toLowerCase() + "%"));
                }
                if (merchantLocation != null && !merchantLocation.isEmpty()) {
                    predicates.add(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("merchantLocation")), "%" +
                                    merchantLocation.toLowerCase() + "%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            List<MerchantDto> merchants = merchantService.getAllMerchants(filterQueries);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Merchants has successfully fetched!", merchants);
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMerchantById(@PathVariable UUID id) {
        try {
            MerchantDto merchant = merchantService.getMerchantById(id);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Merchant has successfully fetched!", merchant);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Object> deleteMerchant(@PathVariable UUID merchantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MerchantDto deletedMerchant = merchantService.deleteMerchant(merchantId, userDetails);
            return ResponseHandler.generateResponseSuccess(HttpStatus.OK,
                    "Merchant has successfully deleted!", deletedMerchant);
        } catch (ApiException e) {
            return ResponseHandler.generateResponseFailed(
                    e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.generateResponseFailed(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
