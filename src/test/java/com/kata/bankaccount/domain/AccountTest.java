package com.kata.bankaccount.domain;

import com.kata.bankaccount.exception.InsufficientBalanceException;
import com.kata.bankaccount.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-10T10:00:00Z"), ZoneId.of("UTC"));

    @Test
    void should_increase_balance_on_deposit() {
        Account account = new Account(fixedClock);

        account.deposit(BigDecimal.valueOf(100));

        assertThat(account.getBalance()).isEqualByComparingTo("100");
    }

    @Test
    void should_decrease_balance_on_withdrawal() {
        Account account = new Account(fixedClock);
        account.deposit(BigDecimal.valueOf(100));

        account.withdraw(BigDecimal.valueOf(40));

        assertThat(account.getBalance()).isEqualByComparingTo("60");
    }

    @Test
    void should_reject_withdrawal_exceeding_balance() {
        Account account = new Account(fixedClock);
        account.deposit(BigDecimal.valueOf(50));

        assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(100)))
                .isInstanceOf(InsufficientBalanceException.class);
        assertThat(account.getBalance()).isEqualByComparingTo("50");
    }

    @Test
    void should_reject_negative_or_zero_amounts() {
        Account account = new Account(fixedClock);

        assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.deposit(BigDecimal.valueOf(-10)))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(-10)))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void should_record_operations_in_chronological_order_with_running_balance() {
        Account account = new Account(fixedClock);

        account.deposit(BigDecimal.valueOf(1000));
        account.withdraw(BigDecimal.valueOf(200));
        account.deposit(BigDecimal.valueOf(300));

        assertThat(account.getOperations()).hasSize(3);
        assertThat(account.getOperations().get(0).type()).isEqualTo(OperationType.DEPOSIT);
        assertThat(account.getOperations().get(0).balanceAfter()).isEqualByComparingTo("1000");
        assertThat(account.getOperations().get(1).type()).isEqualTo(OperationType.WITHDRAWAL);
        assertThat(account.getOperations().get(1).balanceAfter()).isEqualByComparingTo("800");
        assertThat(account.getOperations().get(2).type()).isEqualTo(OperationType.DEPOSIT);
        assertThat(account.getOperations().get(2).balanceAfter()).isEqualByComparingTo("1100");
    }
}
