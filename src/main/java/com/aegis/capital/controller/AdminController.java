package com.aegis.capital.controller;

import com.aegis.capital.config.AccountsConstants;
import com.aegis.capital.entity.Account;
import com.aegis.capital.entity.Transaction;
import com.aegis.capital.entity.User;
import com.aegis.capital.service.AccountService;
import com.aegis.capital.service.TransactionService;
import com.aegis.capital.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AdminController(UserService userService, AccountService accountService,
                           TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        var admin = userService.findByUsername(auth.getName());
        var allTransactions = transactionService.getAllTransactions();

        var recentTransactions = allTransactions.size() > AccountsConstants.MAX_RECENT_TRANSACTIONS_ADMIN
                ? allTransactions.subList(0, AccountsConstants.MAX_RECENT_TRANSACTIONS_ADMIN)
                : allTransactions;

        model.addAttribute(AccountsConstants.MODEL_ADMIN, admin);
        model.addAttribute("userCount", userService.getUserCount());
        model.addAttribute("activeUsers", userService.getActiveUserCount());
        model.addAttribute("inactiveUsers", userService.getInactiveUserCount());
        model.addAttribute("transactionCount", allTransactions.size());
        model.addAttribute(AccountsConstants.MODEL_RECENT_TXNS, recentTransactions);
        model.addAttribute("totalDeposited", transactionService.getTotalDeposited());
        model.addAttribute("totalWithdrawn", transactionService.getTotalWithdrawn());
        model.addAttribute("totalTransferred", transactionService.getTotalTransferred());
        model.addAttribute("totalBankBalance", accountService.getTotalBankBalance());
        model.addAttribute("allTransactions", allTransactions);
        return AccountsConstants.VIEW_ADMIN_DASHBOARD;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return AccountsConstants.VIEW_ADMIN_USERS;
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        var user = userService.findById(id);
        var accounts = accountService.getUserAccounts(id);
        var transactions = transactionService.getTransactionHistory(id);

        var userAccountNumbers = accounts.stream()
                .map(Account::getAccountNumber).collect(Collectors.toSet());

        model.addAttribute(AccountsConstants.MODEL_USER, user);
        model.addAttribute(AccountsConstants.MODEL_ACCOUNTS, accounts);
        model.addAttribute(AccountsConstants.MODEL_TRANSACTIONS, transactions);
        model.addAttribute(AccountsConstants.MODEL_USER_ACCOUNT_NUMBERS, userAccountNumbers);
        return AccountsConstants.VIEW_ADMIN_USER_DETAIL;
    }

    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        model.addAttribute(AccountsConstants.MODEL_TRANSACTIONS, transactionService.getAllTransactions());
        return AccountsConstants.VIEW_ADMIN_TRANSACTIONS;
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id) {
        var user = userService.toggleUserStatus(id);
        log.info("Admin toggled user '{}' to enabled={}", user.getUsername(), user.isEnabled());
        return "redirect:/admin/users/" + id;
    }
}
