package br.com.nandoligeiro.frauddetection.domain.service;

import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.model.FraudDecision;
import br.com.nandoligeiro.frauddetection.domain.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.model.TriggeredRule;
import br.com.nandoligeiro.frauddetection.domain.model.rule.RuleEvaluationResult;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class FraudAlertFactory {

    private final Clock clock;

    public FraudAlertFactory(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public FraudAlert createAlert(Transaction transaction, List<RuleEvaluationResult> triggeredRules) {
        Objects.requireNonNull(transaction, "transaction must not be null");
        if (triggeredRules == null || triggeredRules.isEmpty()) {
            throw new IllegalArgumentException("triggeredRules must not be empty");
        }

        FraudSeverity highestSeverity = triggeredRules.stream()
                .map(RuleEvaluationResult::severity)
                .max(Comparator.comparingInt(FraudAlertFactory::severityWeight))
                .orElse(FraudSeverity.LOW);

        List<TriggeredRule> rules = triggeredRules.stream()
                .filter(RuleEvaluationResult::suspicious)
                .map(RuleEvaluationResult::toTriggeredRule)
                .toList();

        return new FraudAlert(
                "alert-" + UUID.randomUUID(),
                transaction.id().value(),
                transaction.accountId().value(),
                highestSeverity,
                FraudDecision.SUSPICIOUS,
                rules,
                Instant.now(clock)
        );
    }

    private static int severityWeight(FraudSeverity severity) {
        return switch (severity) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
        };
    }
}
