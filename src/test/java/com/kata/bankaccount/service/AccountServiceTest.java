package com.kata.bankaccount.service;

import com.kata.bankaccount.domain.Operation;
import com.kata.bankaccount.exception.AccountNotFoundException;
import com.kata.bankaccount.exception.InsufficientBalanceException;
import com.kata.bankaccount.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(new AccountRepository());
    }

    @Test
    void should_open_account_with_zero_balance() {
        String accountId = accountService.openAccount();

        assertThat(accountService.getBalance(accountId)).isEqualByComparingTo("0");
    }

    @Test
    void should_deposit_and_withdraw() {
        String accountId = accountService.openAccount();

        accountService.deposit(accountId, BigDecimal.valueOf(500));
        accountService.withdraw(accountId, BigDecimal.valueOf(150));

        assertThat(accountService.getBalance(accountId)).isEqualByComparingTo("350");
    }

    @Test
    void should_reject_withdrawal_when_balance_insufficient() {
        String accountId = accountService.openAccount();
        accountService.deposit(accountId, BigDecimal.valueOf(100));

        assertThatThrownBy(() -> accountService.withdraw(accountId, BigDecimal.valueOf(200)))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void should_throw_when_account_unknown() {
        assertThatThrownBy(() -> accountService.getBalance("unknown-id"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void should_expose_operation_history() {
        String accountId = accountService.openAccount();

        accountService.deposit(accountId, BigDecimal.valueOf(1000));
        accountService.withdraw(accountId, BigDecimal.valueOf(300));

        var history = accountService.getHistory(accountId);
        assertThat(history).hasSize(2);
        assertThat(history.get(0).amount()).isEqualByComparingTo("1000");
        assertThat(history.get(1).amount()).isEqualByComparingTo("300");
        Operation last = history.get(history.size() - 1);
        assertThat(last.balanceAfter()).isEqualByComparingTo("700");
    }
}
