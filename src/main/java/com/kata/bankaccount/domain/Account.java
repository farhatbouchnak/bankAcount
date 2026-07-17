package com.kata.bankaccount.domain;

import com.kata.bankaccount.exception.InsufficientBalanceException;
import com.kata.bankaccount.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A bank account. Keeps its balance and the full history of operations
 * in memory - no persistence, as required by the kata.
 */
public class Account {

    private final String id;
    private final Clock clock;
    private final List<Operation> operations = new CopyOnWriteArrayList<>();
    private BigDecimal balance = BigDecimal.ZERO;

    public Account() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Package/test-visible constructor allowing a fixed Clock to be injected,
     * so operation dates are deterministic in tests.
     */
    public Account(Clock clock) {
        this.id = UUID.randomUUID().toString();
        this.clock = clock;
    }

    public synchronized void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
        operations.add(new Operation(LocalDateTime.now(clock), OperationType.DEPOSIT, amount, balance));
    }

    public synchronized void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance: available " + balance + ", requested " + amount);
        }
        balance = balance.subtract(amount);
        operations.add(new Operation(LocalDateTime.now(clock), OperationType.WITHDRAWAL, amount, balance));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be strictly positive");
        }
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /** Operations in chronological order (oldest first). */
    public List<Operation> getOperations() {
        return Collections.unmodifiableList(operations);
    }
}
