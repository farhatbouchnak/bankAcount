package com.kata.bankaccount.service;

import com.kata.bankaccount.domain.Account;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class StatementPrinterTest {

    @Test
    void should_print_statement_with_header_and_running_balance() {
        Clock clock = Clock.fixed(Instant.parse("2024-01-14T10:00:00Z"), ZoneId.of("UTC"));
        Account account = new Account(clock);
        account.deposit(BigDecimal.valueOf(1000));
        account.withdraw(BigDecimal.valueOf(200));

        StatementPrinter printer = new StatementPrinter();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        printer.print(account.getOperations(), new PrintStream(buffer, true, StandardCharsets.UTF_8));

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("DATE", "AMOUNT", "BALANCE");
        assertThat(output).contains("14/01/2024");
        assertThat(output).contains("+1000.00");
        assertThat(output).contains("-200.00");
        assertThat(output).contains("800.00");
    }
}
