package com.challenge_6.challenge_6.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.challenge_6.challenge_6.dto.request.CreateMerchantDto;
import com.challenge_6.challenge_6.dto.request.UpdateMerchantDto;
import com.challenge_6.challenge_6.dto.response.MerchantDto;
import com.challenge_6.challenge_6.entity.Merchant;
import com.challenge_6.challenge_6.entity.Role;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.exception.ApiException;
import com.challenge_6.challenge_6.repository.MerchantRepository;
import com.challenge_6.challenge_6.repository.RoleRepository;
import com.challenge_6.challenge_6.repository.UserRepository;
import com.challenge_6.challenge_6.service.MerchantService;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public MerchantDto createMerchant(CreateMerchantDto request, UserDetails userDetails) {
        Merchant merchant = modelMapper.map(request, Merchant.class);
        Merchant newMerchant = merchantRepository.save(merchant);

        Optional<User> userOnDb = userRepository.findByUsername(userDetails.getUsername());
        User user = userOnDb.get();
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(roleRepository.findByName("ROLE_MERCHANT").get());
        user.setRoles(roleList);
        user.setMerchant(newMerchant);
        userRepository.save(user);

        MerchantDto merchantDto = modelMapper.map(newMerchant, MerchantDto.class);

        return merchantDto;
    }

    @Override
    public MerchantDto updateMerchant(UUID id, UpdateMerchantDto request, UserDetails userDetails) throws ApiException {
        Optional<Merchant> merchantOnDb = merchantRepository.findById(id);

        if (merchantOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "Merchant with coresponding id is not found!");

        if (!merchantOnDb.get().getUser().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't update other merchant data!");

        Merchant existedMerchant = merchantOnDb.get();
        if (request.getMerchantName().isPresent())
            existedMerchant.setMerchantName(request.getMerchantName().get());
        if (request.getMerchantLocation().isPresent())
            existedMerchant.setMerchantLocation(request.getMerchantLocation().get());
        if (request.getIsOpen().isPresent())
            existedMerchant.setIsOpen(request.getIsOpen().get());
        MerchantDto merchantDto = modelMapper.map(merchantRepository.save(existedMerchant), MerchantDto.class);

        return merchantDto;
    }

    @Override
    public List<MerchantDto> getAllMerchants(Specification<Merchant> filterQueries) {
        List<Merchant> merchants = merchantRepository.findAll(filterQueries);
        List<MerchantDto> merchantsDto = merchants.stream().map(m -> modelMapper.map(m, MerchantDto.class))
                .collect(Collectors.toList());
        return merchantsDto;
    }

    @Override
    public MerchantDto getMerchantById(UUID id) throws ApiException {
        Optional<Merchant> merchantOnDb = merchantRepository.findById(id);
        if (merchantOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "Merchant with coresponding id is not found!");

        MerchantDto merchantDto = modelMapper.map(merchantOnDb.get(), MerchantDto.class);
        return merchantDto;
    }

    @Override
    public MerchantDto deleteMerchant(UUID merchantId, UserDetails userDetails) throws ApiException {
        Optional<Merchant> merchantOnDb = merchantRepository.findById(merchantId);

        if (merchantOnDb.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "Merchant with id " + merchantId + " is not found!");

        if (!merchantOnDb.get().getUser().getUsername().equals(userDetails.getUsername()))
            throw new ApiException(HttpStatus.FORBIDDEN, "You can't delete other merchant data!");

        Optional<User> userOnDb = userRepository.findByUsername(userDetails.getUsername());
        User user = userOnDb.get();
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(roleRepository.findByName("ROLE_CUSTOMER").get());
        user.setRoles(roleList);
        user.setMerchant(null);
        userRepository.save(user);

        Merchant deletedMerchant = merchantOnDb.get();
        merchantRepository.delete(deletedMerchant);
        MerchantDto merchantDto = modelMapper.map(deletedMerchant, MerchantDto.class);
        return merchantDto;
    }

}
