package br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.TriggeredRule;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
public class FraudAlertEventMapper {

    private static final String EVENT_TYPE = "FRAUD_ALERT_CREATED";

    private final Clock clock;

    public FraudAlertEventMapper(Clock clock) {
        this.clock = clock;
    }

    public FraudAlertEventPayload toPayload(FraudAlert alert) {
        return new FraudAlertEventPayload(
                "evt-" + UUID.randomUUID(),
                EVENT_TYPE,
                alert.alertId(),
                alert.transactionId(),
                alert.accountId(),
                alert.severity(),
                alert.decision(),
                alert.triggeredRules().stream().map(this::toPayload).toList(),
                alert.createdAt(),
                Instant.now(clock)
        );
    }

    private TriggeredRulePayload toPayload(TriggeredRule rule) {
        return new TriggeredRulePayload(
                rule.ruleId(),
                rule.ruleVersion(),
                rule.reason()
        );
    }
}
