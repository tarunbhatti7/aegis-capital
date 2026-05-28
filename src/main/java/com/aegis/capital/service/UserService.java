package com.aegis.capital.service;

import com.aegis.capital.config.AccountsConstants;
import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.User;
import com.aegis.capital.repository.AccountRepository;
import com.aegis.capital.repository.UserRepository;
import com.aegis.capital.util.AccountNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String username, String password, String email, String fullName, String phone) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException(AccountsConstants.USERNAME_EXISTS);

        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException(AccountsConstants.EMAIL_EXISTS);

        var user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(AccountsConstants.ROLE_USER);

        user = userRepository.save(user);

        var accountNumber = AccountNumberGenerator.generate();
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = AccountNumberGenerator.generate();
        }

        var account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUser(user);

        accountRepository.save(account);
        log.info("User '{}' registered with account {}", username, accountNumber);
        return user;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(AccountsConstants.USER_NOT_FOUND_MSG, username)));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(AccountsConstants.USER_NOT_FOUND_ID_MSG, id)));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() { return userRepository.findAll(); }

    @Transactional(readOnly = true)
    public long getUserCount() { return userRepository.count(); }

    @Transactional(readOnly = true)
    public long getActiveUserCount() { return userRepository.countByEnabled(true); }

    @Transactional(readOnly = true)
    public long getInactiveUserCount() { return userRepository.countByEnabled(false); }

    @Transactional
    public User toggleUserStatus(Long userId) {
        var user = findById(userId);
        var newStatus = !user.isEnabled();
        user.setEnabled(newStatus);
        userRepository.save(user);

        accountRepository.findByUser(user)
                .forEach(account -> {
                    account.setActive(newStatus);
                    accountRepository.save(account);
                });

        log.info("User '{}' status toggled to enabled={}", user.getUsername(), newStatus);
        return user;
    }
}
