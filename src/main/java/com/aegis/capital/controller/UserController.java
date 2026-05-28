package com.aegis.capital.controller;

import com.aegis.capital.config.AccountsConstants;
import com.aegis.capital.dto.TransactionRequest;
import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.Transaction;
import com.aegis.capital.service.AccountService;
import com.aegis.capital.service.TransactionService;
import com.aegis.capital.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public UserController(UserService userService, AccountService accountService,
                          TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        var user = userService.findByUsername(auth.getName());
        var accounts = accountService.getUserAccounts(user.getId());
        var allTransactions = transactionService.getTransactionHistory(user.getId());

        var recentTransactions = allTransactions.size() > AccountsConstants.MAX_RECENT_TRANSACTIONS_USER
                ? allTransactions.subList(0, AccountsConstants.MAX_RECENT_TRANSACTIONS_USER)
                : allTransactions;

        var userAccountNumbers = accounts.stream()
                .map(Account::getAccountNumber).collect(Collectors.toSet());

        model.addAttribute(AccountsConstants.MODEL_USER, user);
        model.addAttribute(AccountsConstants.MODEL_ACCOUNTS, accounts);
        model.addAttribute(AccountsConstants.MODEL_USER_ACCOUNT_NUMBERS, userAccountNumbers);
        model.addAttribute(AccountsConstants.MODEL_RECENT_TXNS, recentTransactions);
        return AccountsConstants.VIEW_USER_DASHBOARD;
    }

    @GetMapping("/accounts")
    public String viewAccounts(Authentication auth, Model model) {
        var user = userService.findByUsername(auth.getName());
        model.addAttribute(AccountsConstants.MODEL_USER, user);
        model.addAttribute(AccountsConstants.MODEL_ACCOUNTS, accountService.getUserAccounts(user.getId()));
        return AccountsConstants.VIEW_ACCOUNT_DETAILS;
    }

    @GetMapping("/deposit")
    public String showDepositForm(Authentication auth, Model model) {
        return showTransactionForm(auth, model, AccountsConstants.VIEW_DEPOSIT);
    }

    @PostMapping("/deposit")
    public String deposit(@Valid @ModelAttribute TransactionRequest request,
                          BindingResult result, Authentication auth, Model model) {
        if (result.hasErrors()) return showTransactionForm(auth, model, AccountsConstants.VIEW_DEPOSIT);
        try {
            transactionService.deposit(request.getAccountNumber(), request.getAmount(),
                    auth.getName(), request.getDescription());
            log.info("Deposit by {}: ${} into {}", auth.getName(), request.getAmount(), request.getAccountNumber());
            return AccountsConstants.REDIRECT_DASHBOARD + AccountsConstants.SUCCESS_DEPOSIT;
        } catch (Exception e) {
            log.error("Deposit failed for {}: {}", auth.getName(), e.getMessage());
            model.addAttribute(AccountsConstants.MODEL_ERROR, e.getMessage());
            return showTransactionForm(auth, model, AccountsConstants.VIEW_DEPOSIT);
        }
    }

    @GetMapping("/withdraw")
    public String showWithdrawForm(Authentication auth, Model model) {
        return showTransactionForm(auth, model, AccountsConstants.VIEW_WITHDRAW);
    }

    @PostMapping("/withdraw")
    public String withdraw(@Valid @ModelAttribute TransactionRequest request,
                           BindingResult result, Authentication auth, Model model) {
        if (result.hasErrors()) return showTransactionForm(auth, model, AccountsConstants.VIEW_WITHDRAW);
        try {
            transactionService.withdraw(request.getAccountNumber(), request.getAmount(),
                    auth.getName(), request.getDescription());
            log.info("Withdraw by {}: ${} from {}", auth.getName(), request.getAmount(), request.getAccountNumber());
            return AccountsConstants.REDIRECT_DASHBOARD + AccountsConstants.SUCCESS_WITHDRAW;
        } catch (Exception e) {
            log.error("Withdraw failed for {}: {}", auth.getName(), e.getMessage());
            model.addAttribute(AccountsConstants.MODEL_ERROR, e.getMessage());
            return showTransactionForm(auth, model, AccountsConstants.VIEW_WITHDRAW);
        }
    }

    @GetMapping("/transfer")
    public String showTransferForm(Authentication auth, Model model) {
        return showTransactionForm(auth, model, AccountsConstants.VIEW_TRANSFER);
    }

    @PostMapping("/transfer")
    public String transfer(@Valid @ModelAttribute TransactionRequest request,
                           BindingResult result, Authentication auth, Model model) {
        if (result.hasErrors()) return showTransactionForm(auth, model, AccountsConstants.VIEW_TRANSFER);
        try {
            transactionService.transfer(request.getAccountNumber(), request.getTargetAccountNumber(),
                    request.getAmount(), auth.getName(), request.getDescription());
            log.info("Transfer by {}: ${} from {} to {}", auth.getName(), request.getAmount(),
                    request.getAccountNumber(), request.getTargetAccountNumber());
            return AccountsConstants.REDIRECT_DASHBOARD + AccountsConstants.SUCCESS_TRANSFER;
        } catch (Exception e) {
            log.error("Transfer failed for {}: {}", auth.getName(), e.getMessage());
            model.addAttribute(AccountsConstants.MODEL_ERROR, e.getMessage());
            return showTransactionForm(auth, model, AccountsConstants.VIEW_TRANSFER);
        }
    }

    @GetMapping("/transactions")
    public String transactionHistory(Authentication auth, Model model) {
        var user = userService.findByUsername(auth.getName());
        var accounts = accountService.getUserAccounts(user.getId());
        var transactions = transactionService.getTransactionHistory(user.getId());

        var userAccountNumbers = accounts.stream()
                .map(Account::getAccountNumber).collect(Collectors.toSet());

        model.addAttribute(AccountsConstants.MODEL_USER, user);
        model.addAttribute(AccountsConstants.MODEL_TRANSACTIONS, transactions);
        model.addAttribute(AccountsConstants.MODEL_USER_ACCOUNT_NUMBERS, userAccountNumbers);
        return AccountsConstants.VIEW_TRANSACTION_HISTORY;
    }

    private String showTransactionForm(Authentication auth, Model model, String view) {
        var user = userService.findByUsername(auth.getName());
        model.addAttribute(AccountsConstants.MODEL_USER, user);
        model.addAttribute(AccountsConstants.MODEL_ACCOUNTS, accountService.getUserAccounts(user.getId()));
        if (!model.containsAttribute(AccountsConstants.MODEL_TXN_REQUEST)) {
            model.addAttribute(AccountsConstants.MODEL_TXN_REQUEST, new TransactionRequest());
        }
        return view;
    }
}
