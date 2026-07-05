package br.com.nandoligeiro.frauddetection.domain.service;

import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.model.FraudDecision;
import br.com.nandoligeiro.frauddetection.domain.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.model.TransactionChannel;
import br.com.nandoligeiro.frauddetection.domain.model.rule.RuleEvaluationResult;
import br.com.nandoligeiro.frauddetection.domain.model.rule.RuleId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.model.vo.TransactionId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FraudAlertFactoryTest {

    @Test
    void shouldCreateAlertWithHighestSeverity() {
        FraudAlertFactory factory = new FraudAlertFactory(
                Clock.fixed(Instant.parse("2026-07-05T10:15:30Z"), ZoneOffset.UTC)
        );

        FraudAlert alert = factory.createAlert(transaction(), List.of(
                RuleEvaluationResult.triggered(RuleId.of("LOW_RULE"), 1, FraudSeverity.LOW, "low risk"),
                RuleEvaluationResult.triggered(RuleId.of("HIGH_RULE"), 1, FraudSeverity.HIGH, "high risk")
        ));

        assertThat(alert.decision()).isEqualTo(FraudDecision.SUSPICIOUS);
        assertThat(alert.severity()).isEqualTo(FraudSeverity.HIGH);
        assertThat(alert.triggeredRules()).hasSize(2);
        assertThat(alert.createdAt()).isEqualTo(Instant.parse("2026-07-05T10:15:30Z"));
    }

    private Transaction transaction() {
        return Transaction.create(
                TransactionId.of("tx-123"),
                AccountId.of("acc-123"),
                CardId.of("card-123"),
                Money.of(new BigDecimal("6000.00"), "BRL"),
                Merchant.of("merchant-123", "5411"),
                TransactionChannel.CARD_NOT_PRESENT,
                GeoLocation.of("US", "MIAMI", 25.7617, -80.1918),
                Instant.parse("2026-07-05T10:15:30Z")
        );
    }
}
