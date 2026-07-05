package br.com.nandoligeiro.frauddetection.domain.model.rule;

import br.com.nandoligeiro.frauddetection.domain.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;

public interface FraudRule {

    RuleId id();

    int version();

    int priority();

    FraudSeverity severity();

    boolean enabled();

    RuleEvaluationResult evaluate(Transaction transaction);
}
