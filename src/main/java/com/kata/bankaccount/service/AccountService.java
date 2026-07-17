package com.kata.bankaccount.service;

import com.kata.bankaccount.domain.Account;
import com.kata.bankaccount.domain.Operation;
import com.kata.bankaccount.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service API for the bank account kata: open an account, deposit,
 * withdraw, and read the operation history.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /** Opens a new, empty account and returns its id. */
    public String openAccount() {
        Account account = new Account();
        accountRepository.save(account);
        return account.getId();
    }

    public void deposit(String accountId, BigDecimal amount) {
        accountRepository.findById(accountId).deposit(amount);
    }

    public void withdraw(String accountId, BigDecimal amount) {
        accountRepository.findById(accountId).withdraw(amount);
    }

    public BigDecimal getBalance(String accountId) {
        return accountRepository.findById(accountId).getBalance();
    }

    /** Full operation history (date, amount, balance), oldest first. */
    public List<Operation> getHistory(String accountId) {
        return accountRepository.findById(accountId).getOperations();
    }
}
