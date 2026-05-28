package com.aegis.capital.config;

import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.User;
import com.aegis.capital.repository.AccountRepository;
import com.aegis.capital.repository.UserRepository;
import com.aegis.capital.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionService transactionService;

    public DataInitializer(UserRepository userRepository, AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already populated, skipping data initialization");
            return;
        }

        log.info("Initializing demo data...");

        var admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@aegiscapital.com");
        admin.setFullName("System Administrator");
        admin.setPhone("+1-555-0001");
        admin.setRole(AccountsConstants.ROLE_ADMIN);
        admin = userRepository.save(admin);

        var adminAccount = new Account();
        adminAccount.setAccountNumber("AEG0000000001");
        adminAccount.setUser(admin);
        adminAccount.setBalance(new BigDecimal("100000.00"));
        accountRepository.save(adminAccount);

        var user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setEmail("john@email.com");
        user1.setFullName("John Smith");
        user1.setPhone("+1-555-0002");
        user1.setRole(AccountsConstants.ROLE_USER);
        user1 = userRepository.save(user1);

        var user1Account = new Account();
        user1Account.setAccountNumber("AEG0000000002");
        user1Account.setUser(user1);
        user1Account.setBalance(new BigDecimal("5000.00"));
        accountRepository.save(user1Account);

        var user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setEmail("jane@email.com");
        user2.setFullName("Jane Doe");
        user2.setPhone("+1-555-0003");
        user2.setRole(AccountsConstants.ROLE_USER);
        user2 = userRepository.save(user2);

        var user2Account = new Account();
        user2Account.setAccountNumber("AEG0000000003");
        user2Account.setUser(user2);
        user2Account.setBalance(new BigDecimal("10000.00"));
        accountRepository.save(user2Account);

        var user3 = new User();
        user3.setUsername("user3");
        user3.setPassword(passwordEncoder.encode("password123"));
        user3.setEmail("bob@email.com");
        user3.setFullName("Bob Wilson");
        user3.setPhone("+1-555-0004");
        user3.setRole(AccountsConstants.ROLE_USER);
        user3 = userRepository.save(user3);

        var user3Account = new Account();
        user3Account.setAccountNumber("AEG0000000004");
        user3Account.setUser(user3);
        user3Account.setBalance(new BigDecimal("7500.00"));
        accountRepository.save(user3Account);

        createSampleTransactions();

        log.info("Demo data initialized successfully");
    }

    private void createSampleTransactions() {
        try {
            transactionService.deposit("AEG0000000002", new BigDecimal("1000.00"),
                    "user1", "Initial deposit");
            transactionService.deposit("AEG0000000003", new BigDecimal("2000.00"),
                    "user2", "Initial deposit");
            transactionService.transfer("AEG0000000002", "AEG0000000003",
                    new BigDecimal("500.00"), "user1", "Test transfer to Jane");
            transactionService.withdraw("AEG0000000003", new BigDecimal("300.00"),
                    "user2", "ATM withdrawal");
            transactionService.transfer("AEG0000000001", "AEG0000000004",
                    new BigDecimal("1000.00"), "admin", "Welcome bonus for Bob");
        } catch (Exception e) {
            log.error("Error creating sample transactions", e);
        }
    }
}
