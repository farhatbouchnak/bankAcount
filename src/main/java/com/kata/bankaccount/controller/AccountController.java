package com.kata.bankaccount.controller;

import com.kata.bankaccount.controller.dto.AccountResponse;
import com.kata.bankaccount.controller.dto.AmountRequest;
import com.kata.bankaccount.domain.Operation;
import com.kata.bankaccount.service.AccountService;
import com.kata.bankaccount.service.StatementPrinter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final StatementPrinter statementPrinter;

    public AccountController(AccountService accountService, StatementPrinter statementPrinter) {
        this.accountService = accountService;
        this.statementPrinter = statementPrinter;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse openAccount() {
        String accountId = accountService.openAccount();
        return toResponse(accountId);
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable String accountId) {
        return toResponse(accountId);
    }

    @PostMapping("/{accountId}/deposits")
    public AccountResponse deposit(@PathVariable String accountId, @Valid @RequestBody AmountRequest request) {
        accountService.deposit(accountId, request.amount());
        return toResponse(accountId);
    }

    @PostMapping("/{accountId}/withdrawals")
    public AccountResponse withdraw(@PathVariable String accountId, @Valid @RequestBody AmountRequest request) {
        accountService.withdraw(accountId, request.amount());
        return toResponse(accountId);
    }

    @GetMapping("/{accountId}/operations")
    public List<Operation> getOperations(@PathVariable String accountId) {
        return accountService.getHistory(accountId);
    }

    @GetMapping(value = "/{accountId}/statement", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getStatement(@PathVariable String accountId) {
        return statementPrinter.format(accountService.getHistory(accountId));
    }

    private AccountResponse toResponse(String accountId) {
        return new AccountResponse(accountId, accountService.getBalance(accountId));
    }
}
