package com.kata.bankaccount.controller.dto;

import java.math.BigDecimal;

public record AccountResponse(String id, BigDecimal balance) {
}
