package br.com.nandoligeiro.frauddetection.application.service;

import br.com.nandoligeiro.frauddetection.application.port.in.EvaluateTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.port.in.FraudDetectionResult;
import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.application.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.model.FraudDecision;
import br.com.nandoligeiro.frauddetection.domain.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.model.TransactionChannel;
import br.com.nandoligeiro.frauddetection.domain.model.rule.FraudRule;
import br.com.nandoligeiro.frauddetection.domain.model.rule.HighAmountRule;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FraudDetectionServiceTest {

    @Test
    void shouldReturnSuspiciousAndPublishAlertWhenRuleIsTriggered() {
        CapturingAlertPublisher alertPublisher = new CapturingAlertPublisher();
        RuleProviderPort ruleProvider = () -> List.of(
                new HighAmountRule(1, 100, true, FraudSeverity.HIGH, Money.of(new BigDecimal("5000.00"), "BRL"))
        );

        FraudDetectionService service = new FraudDetectionService(
                ruleProvider,
                alertPublisher,
                Clock.fixed(Instant.parse("2026-07-05T10:15:30Z"), ZoneOffset.UTC)
        );

        FraudDetectionResult result = service.detect(EvaluateTransactionCommand.of(transactionWithAmount("6000.00")));

        assertThat(result.decision()).isEqualTo(FraudDecision.SUSPICIOUS);
        assertThat(result.hasAlert()).isTrue();
        assertThat(alertPublisher.publishedAlerts).hasSize(1);
    }

    @Test
    void shouldReturnNormalWhenNoRuleIsTriggered() {
        CapturingAlertPublisher alertPublisher = new CapturingAlertPublisher();
        RuleProviderPort ruleProvider = () -> List.of(
                new HighAmountRule(1, 100, true, FraudSeverity.HIGH, Money.of(new BigDecimal("5000.00"), "BRL"))
        );

        FraudDetectionService service = new FraudDetectionService(ruleProvider, alertPublisher, Clock.systemUTC());

        FraudDetectionResult result = service.detect(EvaluateTransactionCommand.of(transactionWithAmount("100.00")));

        assertThat(result.decision()).isEqualTo(FraudDecision.NORMAL);
        assertThat(result.hasAlert()).isFalse();
        assertThat(alertPublisher.publishedAlerts).isEmpty();
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

    private static class CapturingAlertPublisher implements FraudAlertPublisherPort {
        private final List<FraudAlert> publishedAlerts = new ArrayList<>();

        @Override
        public void publish(FraudAlert alert) {
            publishedAlerts.add(alert);
        }
    }
}
