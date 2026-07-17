package com.kata.bankaccount.repository;

import com.kata.bankaccount.domain.Account;
import com.kata.bankaccount.exception.AccountNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pure in-memory storage of accounts for the lifetime of the application.
 * Not a persistence layer - just where account state lives while the app runs.
 */
@Repository
public class AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    public Account findById(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException("No account found with id " + accountId);
        }
        return account;
    }
}
