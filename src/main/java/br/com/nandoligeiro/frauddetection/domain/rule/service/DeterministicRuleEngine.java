package br.com.nandoligeiro.frauddetection.domain.rule.service;

import br.com.nandoligeiro.frauddetection.domain.rule.model.FraudRule;
import br.com.nandoligeiro.frauddetection.domain.rule.model.RuleEvaluationResult;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class DeterministicRuleEngine {
    private final List<FraudRule> rules;

    private DeterministicRuleEngine(List<FraudRule> rules) {
        this.rules = rules.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(FraudRule::priority).reversed())
                .toList();
    }

    public static DeterministicRuleEngine withRules(List<FraudRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("rules must not be empty");
        }
        return new DeterministicRuleEngine(rules);
    }

    public List<RuleEvaluationResult> evaluate(Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction must not be null");
        return rules.stream()
                .filter(FraudRule::enabled)
                .map(rule -> rule.evaluate(transaction))
                .filter(RuleEvaluationResult::suspicious)
                .toList();
    }
}
