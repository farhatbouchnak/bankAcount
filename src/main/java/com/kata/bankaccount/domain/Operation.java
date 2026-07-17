package com.kata.bankaccount.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * An immutable record of a single deposit or withdrawal,
 * capturing the balance right after it happened.
 */
public record Operation(LocalDateTime date, OperationType type, BigDecimal amount, BigDecimal balanceAfter) {
}
