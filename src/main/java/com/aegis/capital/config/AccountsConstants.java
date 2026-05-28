package com.aegis.capital.config;

public final class AccountsConstants {

    private AccountsConstants() {}

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String ACCOUNT_PREFIX = "AEG";
    public static final int ACCOUNT_NUMBER_LENGTH = 13;
    public static final int ACCOUNT_RANDOM_DIGITS = 10;

    public static final int MAX_RECENT_TRANSACTIONS_USER = 5;
    public static final int MAX_RECENT_TRANSACTIONS_ADMIN = 10;

    public static final String DEFAULT_DEPOSIT_DESC = "Deposit";
    public static final String DEFAULT_WITHDRAW_DESC = "Withdrawal";
    public static final String DEFAULT_TRANSFER_DESC = "Transfer";

    public static final String REDIRECT_DASHBOARD = "redirect:/user/dashboard?success=";
    public static final String REDIRECT_DASHBOARD_ERROR = "redirect:/user/dashboard?error=";
    public static final String SUCCESS_DEPOSIT = "Deposit+successful";
    public static final String SUCCESS_WITHDRAW = "Withdrawal+successful";
    public static final String SUCCESS_TRANSFER = "Transfer+successful";

    public static final String MODEL_USER = "user";
    public static final String MODEL_ADMIN = "admin";
    public static final String MODEL_ACCOUNTS = "accounts";
    public static final String MODEL_TRANSACTIONS = "transactions";
    public static final String MODEL_RECENT_TXNS = "recentTransactions";
    public static final String MODEL_ERROR = "error";
    public static final String MODEL_TXN_REQUEST = "transactionRequest";
    public static final String MODEL_USER_ACCOUNT_NUMBERS = "userAccountNumbers";

    public static final String VIEW_LOGIN = "login";
    public static final String VIEW_REGISTER = "register";
    public static final String VIEW_ERROR = "error";
    public static final String VIEW_USER_DASHBOARD = "user-dashboard";
    public static final String VIEW_ACCOUNT_DETAILS = "account-details";
    public static final String VIEW_DEPOSIT = "deposit";
    public static final String VIEW_WITHDRAW = "withdraw";
    public static final String VIEW_TRANSFER = "transfer";
    public static final String VIEW_TRANSACTION_HISTORY = "transaction-history";
    public static final String VIEW_ADMIN_DASHBOARD = "admin-dashboard";
    public static final String VIEW_ADMIN_USERS = "admin-users";
    public static final String VIEW_ADMIN_USER_DETAIL = "admin-user-detail";
    public static final String VIEW_ADMIN_TRANSACTIONS = "admin-transactions";
    public static final String VIEW_REGISTER_REDIRECT = "redirect:/login?registered";

    public static final String LOGIN_ERROR = "Invalid username or password";
    public static final String LOGOUT_MSG = "You have been logged out successfully";
    public static final String PASSWORDS_MISMATCH = "Passwords do not match";

    public static final String ACCOUNT_INACTIVE_MSG = "Account %s is inactive. Cannot process %s.";
    public static final String INSUFFICIENT_BALANCE_MSG = "Insufficient balance in account %s. Available: $%s, Requested: $%s";
    public static final String ACCOUNT_NOT_FOUND_MSG = "Account not found: %s";
    public static final String SOURCE_ACCOUNT_NOT_FOUND_MSG = "Source account not found: %s";
    public static final String DEST_ACCOUNT_NOT_FOUND_MSG = "Destination account not found: %s";
    public static final String USER_NOT_FOUND_MSG = "User not found: %s";
    public static final String USER_NOT_FOUND_ID_MSG = "User not found with id: %s";
    public static final String CONCURRENT_TXN_ERROR = "Transaction failed due to concurrent access. Please try again.";
    public static final String GENERAL_ERROR = "An unexpected error occurred: %s";
    public static final String ACCOUNT_NOT_FOUND_ID_MSG = "Account not found with id: %s";
    public static final String USERNAME_EXISTS = "Username already exists";
    public static final String EMAIL_EXISTS = "Email already exists";
}
