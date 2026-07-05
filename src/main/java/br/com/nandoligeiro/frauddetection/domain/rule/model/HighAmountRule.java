package br.com.nandoligeiro.frauddetection.domain.rule.model;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;

import java.util.Objects;

public final class HighAmountRule implements FraudRule {
    private static final RuleId RULE_ID = RuleId.of("HIGH_AMOUNT");

    private final int version;
    private final int priority;
    private final boolean enabled;
    private final FraudSeverity severity;
    private final Money threshold;

    public HighAmountRule(int version, int priority, boolean enabled, FraudSeverity severity, Money threshold) {
        if (version <= 0) {
            throw new IllegalArgumentException("version must be greater than zero");
        }
        this.version = version;
        this.priority = priority;
        this.enabled = enabled;
        this.severity = Objects.requireNonNull(severity, "severity must not be null");
        this.threshold = Objects.requireNonNull(threshold, "threshold must not be null");
    }

    public RuleId id() { return RULE_ID; }
    public int version() { return version; }
    public int priority() { return priority; }
    public FraudSeverity severity() { return severity; }
    public boolean enabled() { return enabled; }

    public RuleEvaluationResult evaluate(Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction must not be null");
        if (!enabled || !transaction.isHighAmount(threshold)) {
            return RuleEvaluationResult.notTriggered(id(), version, severity);
        }
        return RuleEvaluationResult.triggered(id(), version, severity, "Transaction amount is greater than or equal to threshold " + threshold);
    }
}
