package mrk.domain.model;

import mrk.common.errors.impl.*;
import mrk.domain.model.enums.AccountStatus;
import mrk.domain.model.enums.AccountType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Account {
    private final UUID id;
    private final UUID userId;
    private final AccountType type;
    private final String number;
    private final Money creditLimit;
    private final Money dailyLimit;
    private final Instant createdAt;

    private Money balance;
    private AccountStatus status;

    // ---------- Фабрика ----------
    public static Account createAccount(
            UUID id,
            UUID userId,
            AccountType type,
            String number,
            Money initialBalance,
            Money creditLimit,
            Money dailyLimit,
            Instant createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(number);
        Objects.requireNonNull(initialBalance);
        Objects.requireNonNull(creditLimit);
        Objects.requireNonNull(dailyLimit);
        Objects.requireNonNull(createdAt);

        if (number.isBlank()) {
            throw new InvalidAccountException("Account number cannot be empty");
        }

        if (initialBalance.isNegative()) {
            throw new InvalidAccountException("Initial balance must be non-negative");
        }

        return new Account(
                id, userId, type, number,
                initialBalance, creditLimit, dailyLimit,
                AccountStatus.ACTIVE,
                createdAt
        );
    }

    // ----------- приватный конструктор ------------
    public Account(UUID id,
                   UUID userId,
                   AccountType type,
                   String number,
                   Money balance,
                   Money creditLimit,
                   Money dailyLimit,
                   AccountStatus status,
                   Instant createdAt) {

        this.id = id;
        this.userId = userId;
        this.type = type;
        this.number = number;
        this.balance = balance;
        this.creditLimit = creditLimit;
        this.dailyLimit = dailyLimit;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ---------- Операции ----------
    public void deposit(Money amount) {
        ensureActive();
        ensurePositiveAmount(amount);

        Money newBalance = balance.add(amount);
        validateInvariant(newBalance);

        this.balance = newBalance;
    }

    public void withdraw(Money amount) {
        ensureActive();
        ensurePositiveAmount(amount);

        Money newBalance = balance.subtract(amount);

        validateInvariant(newBalance);

        this.balance = newBalance;
    }

    // ---------- Инварианты ----------
    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountStatusException(
                    "Account in status " + status + " cannot perform operations"
            );
        }
    }

    private void ensurePositiveAmount(Money amount) {
        if (!amount.isPositive()) {
            throw new InvalidAmountException("Amount must be positive");
        }
    }

    /**
     * Инвариант баланса:
     *  - CHECKING/SAVINGS: баланс не может быть < 0;
     *  - CREDIT: баланс может уходить в минус, но не ниже -creditLimit.
     */
    private void validateInvariant(Money newBalance) {
        switch (type) {
            case CHECKING, SAVINGS -> {
                if (newBalance.isNegative()) {
                    throw new InsufficientFundsException(
                            "Баланс не может быть отрицательным для счёта типа " + type
                    );
                }
            }
            case CREDIT -> {
                Money minAllowed = Money.of(
                        creditLimit.getAmount().negate(),
                        creditLimit.getCurrency()
                );

                if (newBalance.compareTo(minAllowed) < 0) {
                    throw new CreditLimitExceededException(
                            "Превышен кредитный лимит: новый баланс = " + newBalance +
                                    ", минимально допустимый = " + minAllowed
                    );
                }
            }
        }
    }

    // ---------- Геттеры ----------

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public AccountType getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public Money getDailyLimit() {
        return dailyLimit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Money getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }
}