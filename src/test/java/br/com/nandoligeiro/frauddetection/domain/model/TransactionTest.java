package br.com.nandoligeiro.frauddetection.domain.model;

import br.com.nandoligeiro.frauddetection.domain.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.model.vo.TransactionId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    @Test
    void shouldIdentifyHighAmountTransaction() {
        Transaction transaction = sampleTransaction(Money.of(new BigDecimal("5000.00"), "BRL"));

        assertThat(transaction.isHighAmount(Money.of(new BigDecimal("3000.00"), "BRL"))).isTrue();
    }

    @Test
    void shouldIdentifyInternationalTransaction() {
        Transaction transaction = Transaction.create(
                TransactionId.of("tx-123"),
                AccountId.of("acc-123"),
                CardId.of("card-123"),
                Money.of(new BigDecimal("100.00"), "BRL"),
                Merchant.of("merchant-123", "5411"),
                TransactionChannel.CARD_PRESENT,
                GeoLocation.of("US", "MIAMI", 25.7617, -80.1918),
                Instant.parse("2026-07-05T10:15:30Z")
        );

        assertThat(transaction.isInternationalComparedTo("BR")).isTrue();
    }

    @Test
    void shouldRejectMoneyWithDifferentCurrencyComparison() {
        Transaction transaction = sampleTransaction(Money.of(new BigDecimal("100.00"), "BRL"));

        assertThatThrownBy(() -> transaction.isHighAmount(Money.of(new BigDecimal("100.00"), "USD")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cannot compare different currencies");
    }

    private Transaction sampleTransaction(Money money) {
        return Transaction.create(
                TransactionId.of("tx-123"),
                AccountId.of("acc-123"),
                CardId.of("card-123"),
                money,
                Merchant.of("merchant-123", "5411"),
                TransactionChannel.CARD_NOT_PRESENT,
                GeoLocation.of("BR", "SAO_PAULO", -23.5505, -46.6333),
                Instant.parse("2026-07-05T10:15:30Z")
        );
    }
}
