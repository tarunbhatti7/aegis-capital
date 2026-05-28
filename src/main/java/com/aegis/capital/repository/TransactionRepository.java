package com.aegis.capital.repository;

import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountOrToAccountOrderByTimestampDesc(Account fromAccount, Account toAccount);

    @Query("SELECT t FROM Transaction t WHERE " +
           "t.fromAccount.id IN (SELECT a.id FROM Account a WHERE a.user.id = :userId) OR " +
           "t.toAccount.id IN (SELECT a.id FROM Account a WHERE a.user.id = :userId) " +
           "ORDER BY t.timestamp DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    List<Transaction> findAllByOrderByTimestampDesc();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'DEPOSIT' AND t.status = 'SUCCESS'")
    BigDecimal sumAllDeposits();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'WITHDRAW' AND t.status = 'SUCCESS'")
    BigDecimal sumAllWithdrawals();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'TRANSFER' AND t.status = 'SUCCESS'")
    BigDecimal sumAllTransfers();
}
