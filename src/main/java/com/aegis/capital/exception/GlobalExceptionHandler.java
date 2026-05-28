package com.aegis.capital.exception;

import com.aegis.capital.config.AccountsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalance(InsufficientBalanceException ex, Model model) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        model.addAttribute(AccountsConstants.MODEL_ERROR, ex.getMessage());
        return AccountsConstants.VIEW_ERROR;
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public String handleAccountNotFound(AccountNotFoundException ex, Model model) {
        log.warn("Account not found: {}", ex.getMessage());
        model.addAttribute(AccountsConstants.MODEL_ERROR, ex.getMessage());
        return AccountsConstants.VIEW_ERROR;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.warn("Bad request: {}", ex.getMessage());
        model.addAttribute(AccountsConstants.MODEL_ERROR, ex.getMessage());
        return AccountsConstants.VIEW_ERROR;
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, Model model) {
        log.warn("Illegal state: {}", ex.getMessage());
        model.addAttribute(AccountsConstants.MODEL_ERROR, ex.getMessage());
        return AccountsConstants.VIEW_ERROR;
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public String handleOptimisticLockingFailure(OptimisticLockingFailureException ex, Model model) {
        log.error("Concurrent transaction conflict: {}", ex.getMessage());
        model.addAttribute(AccountsConstants.MODEL_ERROR, AccountsConstants.CONCURRENT_TXN_ERROR);
        return AccountsConstants.VIEW_ERROR;
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute(AccountsConstants.MODEL_ERROR,
                String.format(AccountsConstants.GENERAL_ERROR, ex.getMessage()));
        return AccountsConstants.VIEW_ERROR;
    }
}
