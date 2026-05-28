package com.aegis.capital.service;

import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.Transaction;
import com.aegis.capital.entity.User;
import com.aegis.capital.exception.AccountNotFoundException;
import com.aegis.capital.exception.InsufficientBalanceException;
import com.aegis.capital.repository.AccountRepository;
import com.aegis.capital.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    private User createUser(String username) {
        var u = new User();
        u.setUsername(username);
        u.setPassword("pass");
        u.setEmail(username + "@test.com");
        u.setFullName("Test User");
        u.setPhone("1234567890");
        u.setRole("ROLE_USER");
        return u;
    }

    private Account createAccount(String accountNumber, User user, BigDecimal balance) {
        var account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUser(user);
        account.setBalance(balance);
        account.setActive(true);
        return account;
    }

    @Test
    void testDeposit_Success() {
        var user = createUser("testuser");
        var account = createAccount("AEG0000000001", user, new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("AEG0000000001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        var result = transactionService.deposit("AEG0000000001", new BigDecimal("50.00"), "testuser", "Test deposit");

        assertNotNull(result);
        assertEquals(new BigDecimal("150.00"), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testDeposit_AccountNotFound() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class,
                () -> transactionService.deposit("INVALID", new BigDecimal("50.00"), "testuser", "Test"));
    }

    @Test
    void testWithdraw_Success() {
        var user = createUser("testuser");
        var account = createAccount("AEG0000000001", user, new BigDecimal("200.00"));

        when(accountRepository.findByAccountNumber("AEG0000000001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        var result = transactionService.withdraw("AEG0000000001", new BigDecimal("50.00"), "testuser", "Test withdrawal");

        assertNotNull(result);
        assertEquals(new BigDecimal("150.00"), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        var user = createUser("testuser");
        var account = createAccount("AEG0000000001", user, new BigDecimal("30.00"));

        when(accountRepository.findByAccountNumber("AEG0000000001")).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.withdraw("AEG0000000001", new BigDecimal("50.00"), "testuser", "Test"));
    }

    @Test
    void testTransfer_Success() {
        var user1 = createUser("user1");
        var user2 = createUser("user2");
        var fromAccount = createAccount("AEG0000000001", user1, new BigDecimal("500.00"));
        var toAccount = createAccount("AEG0000000002", user2, new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("AEG0000000001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("AEG0000000002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(fromAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        var result = transactionService.transfer("AEG0000000001", "AEG0000000002",
                new BigDecimal("200.00"), "user1", "Test transfer");

        assertNotNull(result);
        assertEquals(new BigDecimal("300.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), toAccount.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testTransfer_InsufficientBalance() {
        var user1 = createUser("user1");
        var user2 = createUser("user2");
        var fromAccount = createAccount("AEG0000000001", user1, new BigDecimal("50.00"));
        var toAccount = createAccount("AEG0000000002", user2, new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("AEG0000000001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("AEG0000000002")).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.transfer("AEG0000000001", "AEG0000000002",
                        new BigDecimal("200.00"), "user1", "Test"));
    }

    @Test
    void testTransfer_DestinationAccountNotFound() {
        var user1 = createUser("user1");
        var fromAccount = createAccount("AEG0000000001", user1, new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("AEG0000000001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.transfer("AEG0000000001", "INVALID",
                        new BigDecimal("200.00"), "user1", "Test"));
    }
}
