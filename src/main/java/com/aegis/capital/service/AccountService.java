package com.aegis.capital.service;

import com.aegis.capital.config.AccountsConstants;
import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.User;
import com.aegis.capital.repository.AccountRepository;
import com.aegis.capital.util.AccountNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(User user) {
        var accountNumber = AccountNumberGenerator.generate();
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = AccountNumberGenerator.generate();
        }

        var account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);

        account = accountRepository.save(account);
        log.info("Account {} created for user {}", accountNumber, user.getUsername());
        return account;
    }

    @Transactional(readOnly = true)
    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(AccountsConstants.ACCOUNT_NOT_FOUND_MSG, accountNumber)));
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(AccountsConstants.ACCOUNT_NOT_FOUND_ID_MSG, id)));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBankBalance() { return accountRepository.sumAllActiveBalances(); }

    @Transactional(readOnly = true)
    public long getActiveAccountCount() { return accountRepository.countByActive(true); }

    @Transactional(readOnly = true)
    public long getInactiveAccountCount() { return accountRepository.countByActive(false); }
}
