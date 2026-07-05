package br.com.nandoligeiro.frauddetection.domain.rule.model;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.TriggeredRule;

public record RuleEvaluationResult(boolean suspicious, RuleId ruleId, int ruleVersion, FraudSeverity severity, String reason) {
    public RuleEvaluationResult {
        if (ruleId == null) {
            throw new IllegalArgumentException("ruleId must not be null");
        }
        if (ruleVersion <= 0) {
            throw new IllegalArgumentException("ruleVersion must be greater than zero");
        }
        if (severity == null) {
            throw new IllegalArgumentException("severity must not be null");
        }
        if (suspicious && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("reason must not be blank when rule is triggered");
        }
    }

    public static RuleEvaluationResult triggered(RuleId ruleId, int ruleVersion, FraudSeverity severity, String reason) {
        return new RuleEvaluationResult(true, ruleId, ruleVersion, severity, reason);
    }

    public static RuleEvaluationResult notTriggered(RuleId ruleId, int ruleVersion, FraudSeverity severity) {
        return new RuleEvaluationResult(false, ruleId, ruleVersion, severity, null);
    }

    public TriggeredRule toTriggeredRule() {
        if (!suspicious) {
            throw new IllegalStateException("only suspicious results can be converted to TriggeredRule");
        }
        return new TriggeredRule(ruleId.value(), ruleVersion, reason);
    }
}
