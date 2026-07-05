package br.com.nandoligeiro.frauddetection.domain.rule.model;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;

import java.util.Objects;

public final class InternationalCardNotPresentRule implements FraudRule {

    private static final RuleId RULE_ID = RuleId.of("INTERNATIONAL_CARD_NOT_PRESENT");

    private final int version;
    private final int priority;
    private final boolean enabled;
    private final FraudSeverity severity;
    private final String homeCountry;

    public InternationalCardNotPresentRule(
            int version,
            int priority,
            boolean enabled,
            FraudSeverity severity,
            String homeCountry
    ) {
        if (version <= 0) {
            throw new IllegalArgumentException("version must be greater than zero");
        }
        if (homeCountry == null || homeCountry.isBlank()) {
            throw new IllegalArgumentException("homeCountry must not be blank");
        }
        this.version = version;
        this.priority = priority;
        this.enabled = enabled;
        this.severity = Objects.requireNonNull(severity, "severity must not be null");
        this.homeCountry = homeCountry;
    }

    @Override
    public RuleId id() {
        return RULE_ID;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public FraudSeverity severity() {
        return severity;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public RuleEvaluationResult evaluate(Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction must not be null");

        boolean triggered = enabled
                && transaction.isCardNotPresent()
                && transaction.isInternationalComparedTo(homeCountry);

        if (!triggered) {
            return RuleEvaluationResult.notTriggered(id(), version, severity);
        }

        return RuleEvaluationResult.triggered(
                id(),
                version,
                severity,
                "International card-not-present transaction detected"
        );
    }
}
