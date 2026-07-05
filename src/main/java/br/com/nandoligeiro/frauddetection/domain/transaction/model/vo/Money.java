package br.com.nandoligeiro.frauddetection.domain.transaction.model.vo;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
        return new Money(amount, Currency.getInstance(currencyCode.toUpperCase()));
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        ensureSameCurrency(other);
        return amount.compareTo(other.amount) >= 0;
    }

    private void ensureSameCurrency(Money other) {
        Objects.requireNonNull(other, "other money must not be null");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("cannot compare different currencies");
        }
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount;
    }
}
