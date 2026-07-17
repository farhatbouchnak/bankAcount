package com.kata.bankaccount.controller;

import com.kata.bankaccount.controller.dto.AccountResponse;
import com.kata.bankaccount.controller.dto.AmountRequest;
import com.kata.bankaccount.controller.dto.ErrorResponse;
import com.kata.bankaccount.domain.Operation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void should_open_deposit_withdraw_and_read_history_through_rest_api() {
        AccountResponse opened = restTemplate.postForObject(baseUrl("/api/accounts"), null, AccountResponse.class);
        assertThat(opened.balance()).isEqualByComparingTo("0");
        String accountId = opened.id();

        AccountResponse afterDeposit = restTemplate.postForObject(
                baseUrl("/api/accounts/" + accountId + "/deposits"),
                new AmountRequest(BigDecimal.valueOf(1000)),
                AccountResponse.class);
        assertThat(afterDeposit.balance()).isEqualByComparingTo("1000");

        AccountResponse afterWithdrawal = restTemplate.postForObject(
                baseUrl("/api/accounts/" + accountId + "/withdrawals"),
                new AmountRequest(BigDecimal.valueOf(200)),
                AccountResponse.class);
        assertThat(afterWithdrawal.balance()).isEqualByComparingTo("800");

        ResponseEntity<Operation[]> operations =
                restTemplate.getForEntity(baseUrl("/api/accounts/" + accountId + "/operations"), Operation[].class);
        assertThat(operations.getBody()).hasSize(2);

        ResponseEntity<String> statement =
                restTemplate.getForEntity(baseUrl("/api/accounts/" + accountId + "/statement"), String.class);
        assertThat(statement.getBody()).contains("DATE", "AMOUNT", "BALANCE");
    }

    @Test
    void should_return_404_for_unknown_account() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity(baseUrl("/api/accounts/does-not-exist"), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_return_422_when_withdrawal_exceeds_balance() {
        AccountResponse opened = restTemplate.postForObject(baseUrl("/api/accounts"), null, AccountResponse.class);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl("/api/accounts/" + opened.id() + "/withdrawals"),
                new AmountRequest(BigDecimal.valueOf(50)),
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void should_return_400_for_non_positive_amount() {
        AccountResponse opened = restTemplate.postForObject(baseUrl("/api/accounts"), null, AccountResponse.class);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl("/api/accounts/" + opened.id() + "/deposits"),
                new AmountRequest(BigDecimal.ZERO),
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
