package com.aegis.capital.service;

import com.aegis.capital.config.AccountsConstants;
import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.Transaction;
import com.aegis.capital.entity.Transaction.TransactionStatus;
import com.aegis.capital.entity.Transaction.TransactionType;
import com.aegis.capital.exception.AccountNotFoundException;
import com.aegis.capital.exception.InsufficientBalanceException;
import com.aegis.capital.repository.AccountRepository;
import com.aegis.capital.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    private Account findActiveAccount(String accountNumber, String context) {
        var account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format(AccountsConstants.ACCOUNT_NOT_FOUND_MSG, accountNumber)));

        if (!account.isActive()) {
            throw new IllegalStateException(
                    String.format(AccountsConstants.ACCOUNT_INACTIVE_MSG, accountNumber, context));
        }
        return account;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction deposit(String accountNumber, BigDecimal amount, String initiatedBy, String description) {
        var account = findActiveAccount(accountNumber, "deposit");
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        var txn = new Transaction();
        txn.setToAccount(account);
        txn.setAmount(amount);
        txn.setTransactionType(TransactionType.DEPOSIT);
        txn.setInitiatedBy(initiatedBy);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription(Optional.ofNullable(description).orElse(AccountsConstants.DEFAULT_DEPOSIT_DESC));

        txn = transactionRepository.save(txn);
        log.info("Deposit ${} into {} by {}", amount, accountNumber, initiatedBy);
        return txn;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction withdraw(String accountNumber, BigDecimal amount, String initiatedBy, String description) {
        var account = findActiveAccount(accountNumber, "withdrawal");

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    String.format(AccountsConstants.INSUFFICIENT_BALANCE_MSG,
                            accountNumber, account.getBalance(), amount));
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        var txn = new Transaction();
        txn.setFromAccount(account);
        txn.setAmount(amount);
        txn.setTransactionType(TransactionType.WITHDRAW);
        txn.setInitiatedBy(initiatedBy);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription(Optional.ofNullable(description).orElse(AccountsConstants.DEFAULT_WITHDRAW_DESC));

        txn = transactionRepository.save(txn);
        log.info("Withdraw ${} from {} by {}", amount, accountNumber, initiatedBy);
        return txn;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction transfer(String fromAccountNumber, String toAccountNumber,
                                BigDecimal amount, String initiatedBy, String description) {
        var fromAccount = findActiveAccount(fromAccountNumber, "transfer");
        var toAccount = findActiveAccount(toAccountNumber, "transfer");

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    String.format(AccountsConstants.INSUFFICIENT_BALANCE_MSG,
                            fromAccountNumber, fromAccount.getBalance(), amount));
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        var txn = new Transaction();
        txn.setFromAccount(fromAccount);
        txn.setToAccount(toAccount);
        txn.setAmount(amount);
        txn.setTransactionType(TransactionType.TRANSFER);
        txn.setInitiatedBy(initiatedBy);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription(Optional.ofNullable(description)
                .orElseGet(() -> String.format("%s of $%s from %s to %s",
                        AccountsConstants.DEFAULT_TRANSFER_DESC, amount,
                        fromAccountNumber, toAccountNumber)));

        txn = transactionRepository.save(txn);
        log.info("Transfer ${} from {} to {} by {}", amount, fromAccountNumber, toAccountNumber, initiatedBy);
        return txn;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsForAccount(Long accountId) {
        var ref = accountRepository.getReferenceById(accountId);
        return transactionRepository.findByFromAccountOrToAccountOrderByTimestampDesc(ref, ref);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalDeposited() { return transactionRepository.sumAllDeposits(); }

    @Transactional(readOnly = true)
    public BigDecimal getTotalWithdrawn() { return transactionRepository.sumAllWithdrawals(); }

    @Transactional(readOnly = true)
    public BigDecimal getTotalTransferred() { return transactionRepository.sumAllTransfers(); }
}
