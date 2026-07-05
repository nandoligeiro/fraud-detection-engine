package br.com.nandoligeiro.frauddetection.application.service;

import br.com.nandoligeiro.frauddetection.application.port.in.DetectFraudUseCase;
import br.com.nandoligeiro.frauddetection.application.port.in.EvaluateTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.port.in.FraudDetectionResult;
import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.application.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.model.rule.FraudRule;
import br.com.nandoligeiro.frauddetection.domain.model.rule.RuleEvaluationResult;
import br.com.nandoligeiro.frauddetection.domain.service.DeterministicRuleEngine;
import br.com.nandoligeiro.frauddetection.domain.service.FraudAlertFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class FraudDetectionService implements DetectFraudUseCase {

    private final RuleProviderPort ruleProvider;
    private final FraudAlertPublisherPort alertPublisher;
    private final FraudAlertFactory alertFactory;

    public FraudDetectionService(
            RuleProviderPort ruleProvider,
            FraudAlertPublisherPort alertPublisher,
            Clock clock
    ) {
        this.ruleProvider = ruleProvider;
        this.alertPublisher = alertPublisher;
        this.alertFactory = new FraudAlertFactory(clock);
    }

    @Override
    public FraudDetectionResult detect(EvaluateTransactionCommand command) {
        Transaction transaction = command.transaction();
        List<FraudRule> rules = ruleProvider.loadRules();

        if (rules.isEmpty()) {
            return FraudDetectionResult.normal(transaction);
        }

        List<RuleEvaluationResult> triggeredRules = DeterministicRuleEngine
                .withRules(rules)
                .evaluate(transaction);

        if (triggeredRules.isEmpty()) {
            return FraudDetectionResult.normal(transaction);
        }

        FraudAlert alert = alertFactory.createAlert(transaction, triggeredRules);
        alertPublisher.publish(alert);

        return FraudDetectionResult.suspicious(transaction, alert);
    }
}
