package mrk.domain.model;

import mrk.common.errors.impl.CurrencyMismatchException;
import mrk.domain.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {
    private final BigDecimal amount;
    private final CurrencyType currency;

    private Money(BigDecimal amount, CurrencyType currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal raw, CurrencyType currency) {
        Objects.requireNonNull(raw, "raw amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");

        if (raw.scale() > 2) {
            throw new IllegalArgumentException("Scale > 2: " + raw);
        }

        BigDecimal normalized = raw.setScale(2, RoundingMode.UNNECESSARY);

        return new Money(normalized, currency);
    }

    public static Money zero(CurrencyType currency) {
        return Money.of(BigDecimal.ZERO, currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    private void ensureSameCurrency(Money other) {
        if (this.currency != other.currency) {
            throw new CurrencyMismatchException(
                    "Различные валюты: " + this.currency + " vs " + other.currency
            );
        }
    }

    @Override
    public int compareTo(Money other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    // ---------- Геттеры ----------

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
