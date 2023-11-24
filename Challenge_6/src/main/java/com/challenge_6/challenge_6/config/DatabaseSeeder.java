package com.challenge_6.challenge_6.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.challenge_6.challenge_6.entity.Merchant;
import com.challenge_6.challenge_6.entity.Privilege;
import com.challenge_6.challenge_6.entity.Product;
import com.challenge_6.challenge_6.entity.Role;
import com.challenge_6.challenge_6.entity.User;
import com.challenge_6.challenge_6.repository.MerchantRepository;
import com.challenge_6.challenge_6.repository.PrivilegeRepository;
import com.challenge_6.challenge_6.repository.ProductRepository;
import com.challenge_6.challenge_6.repository.RoleRepository;
import com.challenge_6.challenge_6.repository.UserRepository;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import jakarta.transaction.Transactional;

@Component
public class DatabaseSeeder implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;

        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_MERCHANT", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_CUSTOMER", Arrays.asList(readPrivilege));

        createUserIfNotFound("admin", "ROLE_ADMIN");
        createUserIfNotFound("customer", "ROLE_CUSTOMER");
        createUserIfNotFound("customer2", "ROLE_CUSTOMER");

        User userMerchant = createUserIfNotFound("merchant", "ROLE_MERCHANT");
        createProduct(userMerchant.getMerchant());
        createProduct(userMerchant.getMerchant());
        createProduct(userMerchant.getMerchant());
        createProduct(userMerchant.getMerchant());
        createProduct(userMerchant.getMerchant());

        User userMerchant2 = createUserIfNotFound("merchant2", "ROLE_MERCHANT");
        createProduct(userMerchant2.getMerchant());
        createProduct(userMerchant2.getMerchant());
        createProduct(userMerchant2.getMerchant());
        createProduct(userMerchant2.getMerchant());
        createProduct(userMerchant2.getMerchant());

        alreadySetup = true;
    }

    @Transactional
    Product createProduct(Merchant merchant) {
        Faker faker = new Faker(new Locale("in-ID"));
        FakeValuesService fakeValuesService = new FakeValuesService(
                new Locale("in-ID"), new RandomService());

        Product product = new Product();
        product.setMerchant(merchant);
        product.setProductName(faker.food().fruit());
        product.setPrice(Double.parseDouble(fakeValuesService.numerify("####.0")));

        return productRepository.save(product);
    }

    @Transactional
    User createUserIfNotFound(String username, String userRole) {
        Optional<User> userOnDb = userRepository.findByUsername(username);

        if (userOnDb.isPresent())
            return userOnDb.get();

        Faker faker = new Faker(new Locale("in-ID"));
        Optional<Role> role = roleRepository.findByName(userRole);

        User user = new User();
        user.setUsername(username);
        user.setEmailAddress(username + "@gmail.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Arrays.asList(role.get()));
        user.setEnabled(true);

        if (userRole == "ROLE_MERCHANT") {
            Merchant merchant = new Merchant();
            merchant.setIsOpen(false);
            merchant.setMerchantName(faker.company().industry());
            merchant.setMerchantLocation(faker.address().city());
            merchant.setUser(user);
            user.setMerchant(merchantRepository.save(merchant));
        }

        return userRepository.save(user);
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Optional<Privilege> privilegeOnDb = privilegeRepository.findByName(name);
        if (privilegeOnDb.isPresent())
            return privilegeOnDb.get();

        Privilege privilege = new Privilege();
        privilege.setName(name);
        privilegeRepository.save(privilege);
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Optional<Role> roleOnDb = roleRepository.findByName(name);
        if (roleOnDb.isPresent())
            return roleOnDb.get();

        Role role = new Role();
        role.setName(name);
        role.setPrivileges(privileges);
        roleRepository.save(role);
        return role;
    }
}
