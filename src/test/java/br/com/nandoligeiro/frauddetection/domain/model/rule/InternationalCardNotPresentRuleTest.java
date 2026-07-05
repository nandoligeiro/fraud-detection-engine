package br.com.nandoligeiro.frauddetection.domain.rule.model;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.TransactionChannel;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.TransactionId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InternationalCardNotPresentRuleTest {

    @Test
    void shouldTriggerWhenTransactionIsInternationalAndCardNotPresent() {
        InternationalRemoteRule rule = new InternationalRemoteRule(
                1,
                90,
                true,
                FraudSeverity.MEDIUM,
                "BR"
        );

        RuleEvaluationResult result = rule.evaluate(transaction("US", TransactionChannel.CARD_NOT_PRESENT));

        assertThat(result.suspicious()).isTrue();
        assertThat(result.ruleId()).isEqualTo(RuleId.of("INTERNATIONAL_CARD_NOT_PRESENT"));
    }

    @Test
    void shouldNotTriggerWhenTransactionIsDomestic() {
        InternationalRemoteRule rule = new InternationalRemoteRule(
                1,
                90,
                true,
                FraudSeverity.MEDIUM,
                "BR"
        );

        RuleEvaluationResult result = rule.evaluate(transaction("BR", TransactionChannel.CARD_NOT_PRESENT));

        assertThat(result.suspicious()).isFalse();
    }

    private Transaction transaction(String country, TransactionChannel channel) {
        return Transaction.create(
                TransactionId.of("tx-123"),
                AccountId.of("acc-123"),
                CardId.of("card-123"),
                Money.of(new BigDecimal("100.00"), "BRL"),
                Merchant.of("merchant-123", "5411"),
                channel,
                GeoLocation.of(country, "CITY", 25.7617, -80.1918),
                Instant.parse("2026-07-05T10:15:30Z")
        );
    }
}
