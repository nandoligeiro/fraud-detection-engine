package br.com.nandoligeiro.frauddetection.application.detection.service;

import br.com.nandoligeiro.frauddetection.application.detection.port.in.DetectFraudUseCase;
import br.com.nandoligeiro.frauddetection.application.detection.port.in.EvaluateTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.detection.port.in.FraudDetectionResult;
import br.com.nandoligeiro.frauddetection.application.detection.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.application.detection.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.fraud.service.FraudAlertFactory;
import br.com.nandoligeiro.frauddetection.domain.rule.model.RuleEvaluationResult;
import br.com.nandoligeiro.frauddetection.domain.rule.service.DeterministicRuleEngine;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class FraudDetectionService implements DetectFraudUseCase {

    private final RuleProviderPort ruleProvider;
    private final FraudAlertPublisherPort publisher;
    private final FraudAlertFactory factory;

    public FraudDetectionService(RuleProviderPort ruleProvider, FraudAlertPublisherPort publisher, Clock clock) {
        this.ruleProvider = ruleProvider;
        this.publisher = publisher;
        this.factory = new FraudAlertFactory(clock);
    }

    @Override
    public FraudDetectionResult detect(EvaluateTransactionCommand command) {
        Transaction transaction = command.transaction();
        var rules = ruleProvider.loadRules();
        if (rules.isEmpty()) {
            return FraudDetectionResult.normal(transaction);
        }
        List<RuleEvaluationResult> results = DeterministicRuleEngine.withRules(rules).evaluate(transaction);
        if (results.isEmpty()) {
            return FraudDetectionResult.normal(transaction);
        }
        FraudAlert alert = factory.createAlert(transaction, results);
        publisher.publish(alert);
        return FraudDetectionResult.suspicious(transaction, alert);
    }
}
