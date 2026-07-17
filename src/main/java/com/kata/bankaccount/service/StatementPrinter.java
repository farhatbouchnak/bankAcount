package com.kata.bankaccount.service;

import com.kata.bankaccount.domain.Operation;
import com.kata.bankaccount.domain.OperationType;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formats a list of operations into a printable bank statement:
 * DATE | AMOUNT | BALANCE
 */
@Service
public class StatementPrinter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String HEADER = String.format("%-12s | %10s | %10s", "DATE", "AMOUNT", "BALANCE");

    /** Builds the statement as a single string, one operation per line. */
    public String format(List<Operation> operations) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        for (Operation operation : operations) {
            sb.append(formatLine(operation)).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** Prints the statement to standard output. */
    public void print(List<Operation> operations) {
        print(operations, System.out);
    }

    /** Prints the statement to the given stream (useful for testing). */
    public void print(List<Operation> operations, PrintStream out) {
        out.print(format(operations));
    }

    private String formatLine(Operation operation) {
        BigDecimal signedAmount = operation.type() == OperationType.WITHDRAWAL
                ? operation.amount().negate()
                : operation.amount();
        return String.format("%-12s | %10s | %10s",
                operation.date().format(DATE_FORMAT),
                formatAmount(signedAmount),
                formatAmount(operation.balanceAfter()));
    }

    private String formatAmount(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        return (scaled.signum() >= 0 ? "+" : "") + scaled;
    }
}
