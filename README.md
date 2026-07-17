# Bank Account Kata

A minimal Spring Boot implementation of the classic Bank Account Kata.

No UI, no database — just a service API and its implementation, backed by
in-memory state.

## Features

- **Deposit / Withdrawal** — `AccountService.deposit()` / `withdraw()`
- **Account statement** — every operation is recorded with its date, amount,
  and resulting balance (`Operation` record)
- **Statement printing** — `StatementPrinter` formats the history as a
  readable statement and can print it to any `PrintStream`

## Requirements

- Java 17+
- Maven 3.8+

## Build & test

```bash
mvn clean install
mvn test
```

## Project structure

```
src/main/java/com/kata/bankaccount
├── BankAccountApplication.java     # Spring Boot entry point
├── domain
│   ├── Account.java                # balance + operation history
│   ├── Operation.java              # date, type, amount, balanceAfter
│   └── OperationType.java          # DEPOSIT / WITHDRAWAL
├── exception
│   ├── InvalidAmountException.java
│   ├── InsufficientBalanceException.java
│   └── AccountNotFoundException.java
├── repository
│   └── AccountRepository.java      # in-memory storage (no persistence)
└── service
    ├── AccountService.java         # the service API
    └── StatementPrinter.java       # formats/prints statements
```

## Usage example

```java
@Autowired
AccountService accountService;

@Autowired
StatementPrinter statementPrinter;

String accountId = accountService.openAccount();

accountService.deposit(accountId, BigDecimal.valueOf(1000));
accountService.withdraw(accountId, BigDecimal.valueOf(200));

statementPrinter.print(accountService.getHistory(accountId));
```

Output:

```
DATE         |     AMOUNT |    BALANCE
14/01/2024   |   +1000.00 |    1000.00
14/01/2024   |    -200.00 |     800.00
```

## Design notes

- `Account` is the aggregate that owns balance and operation history and
  enforces invariants (positive amounts, no overdraft).
- `AccountRepository` is a plain in-memory map — it exists to hold account
  state for the lifetime of the application, not as a persistence layer.
- `Operation` is an immutable record, so a statement line can never be
  mutated after the fact.
- `Account` accepts an injectable `Clock`, so operation dates are
  deterministic and easy to test.
