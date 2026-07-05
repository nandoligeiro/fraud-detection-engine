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

class HighAmountRuleTest {

    @Test
    void shouldTriggerWhenAmountIsGreaterThanThreshold() {
        HighAmountRule rule = new HighAmountRule(
                1,
                100,
                true,
                FraudSeverity.HIGH,
                Money.of(new BigDecimal("5000.00"), "BRL")
        );

        RuleEvaluationResult result = rule.evaluate(transactionWithAmount("6000.00"));

        assertThat(result.suspicious()).isTrue();
        assertThat(result.ruleId()).isEqualTo(RuleId.of("HIGH_AMOUNT"));
        assertThat(result.severity()).isEqualTo(FraudSeverity.HIGH);
    }

    @Test
    void shouldNotTriggerWhenRuleIsDisabled() {
        HighAmountRule rule = new HighAmountRule(
                1,
                100,
                false,
                FraudSeverity.HIGH,
                Money.of(new BigDecimal("5000.00"), "BRL")
        );

        RuleEvaluationResult result = rule.evaluate(transactionWithAmount("6000.00"));

        assertThat(result.suspicious()).isFalse();
    }

    private Transaction transactionWithAmount(String amount) {
        return Transaction.create(
                TransactionId.of("tx-123"),
                AccountId.of("acc-123"),
                CardId.of("card-123"),
                Money.of(new BigDecimal(amount), "BRL"),
                Merchant.of("merchant-123", "5411"),
                TransactionChannel.CARD_PRESENT,
                GeoLocation.of("BR", "SAO_PAULO", -23.5505, -46.6333),
                Instant.parse("2026-07-05T10:15:30Z")
        );
    }
}
