package com.aegis.capital.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    Long id,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    String transactionType,
    String status,
    String description,
    String initiatedBy,
    LocalDateTime timestamp
) {}
