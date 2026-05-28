package com.aegis.capital.dto;

import com.aegis.capital.entity.Transaction;
import java.util.Optional;

public final class TransactionMapper {

    private TransactionMapper() {}

    public static TransactionDTO toDto(Transaction txn) {
        return new TransactionDTO(
            txn.getId(),
            Optional.ofNullable(txn.getFromAccount()).map(a -> a.getAccountNumber()).orElse(null),
            Optional.ofNullable(txn.getToAccount()).map(a -> a.getAccountNumber()).orElse(null),
            txn.getAmount(),
            txn.getTransactionType().name(),
            txn.getStatus().name(),
            txn.getDescription(),
            txn.getInitiatedBy(),
            txn.getTimestamp()
        );
    }
}
