package br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka;

import br.com.nandoligeiro.frauddetection.domain.model.FraudDecision;
import br.com.nandoligeiro.frauddetection.domain.model.FraudSeverity;

import java.time.Instant;
import java.util.List;

public record FraudAlertEventPayload(
        String eventId,
        String eventType,
        String alertId,
        String transactionId,
        String accountId,
        FraudSeverity severity,
        FraudDecision decision,
        List<TriggeredRulePayload> triggeredRules,
        Instant createdAt,
        Instant publishedAt
) {
}
