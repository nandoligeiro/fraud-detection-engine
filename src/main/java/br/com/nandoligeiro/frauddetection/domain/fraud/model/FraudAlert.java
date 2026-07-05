package br.com.nandoligeiro.frauddetection.domain.fraud.model;

import java.time.Instant;
import java.util.List;

public record FraudAlert(
        String alertId,
        String transactionId,
        String accountId,
        FraudSeverity severity,
        FraudDecision decision,
        List<TriggeredRule> triggeredRules,
        Instant createdAt
) {
}
