package br.com.nandoligeiro.frauddetection.domain.rule.model;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;

public interface FraudRule {

    RuleId id();

    int version();

    int priority();

    FraudSeverity severity();

    boolean enabled();

    RuleEvaluationResult evaluate(Transaction transaction);
}
