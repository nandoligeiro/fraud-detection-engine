package br.com.nandoligeiro.frauddetection.application.detection.service;

import br.com.nandoligeiro.frauddetection.application.detection.port.in.DetectFraudUseCase;
import br.com.nandoligeiro.frauddetection.application.detection.port.in.EvaluateTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.detection.port.in.FraudDetectionResult;
import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.application.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.fraud.service.FraudAlertFactory;
import br.com.nandoligeiro.frauddetection.domain.rule.model.FraudRule;
import br.com.nandoligeiro.frauddetection.domain.rule.model.RuleEvaluationResult;
import br.com.nandoligeiro.frauddetection.domain.rule.service.DeterministicRuleEngine;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class FraudDetectionService implements DetectFraudUseCase {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionService.class);

    private final RuleProviderPort ruleProvider;
    private final FraudAlertPublisherPort alertPublisher;
    private final FraudAlertFactory alertFactory;

    public FraudDetectionService(RuleProviderPort ruleProvider, FraudAlertPublisherPort alertPublisher, Clock clock) {
        this.ruleProvider = ruleProvider;
        this.alertPublisher = alertPublisher;
        this.alertFactory = new FraudAlertFactory(clock);
    }

    @Override
    public FraudDetectionResult detect(EvaluateTransactionCommand command) {
        Transaction transaction = command.transaction();
        List<FraudRule> rules = ruleProvider.loadRules();

        log.info("detection started transactionId={} accountId={} rulesLoaded={}", transaction.id().value(), transaction.accountId().value(), rules.size());

        if (rules.isEmpty()) {
            log.info("detection finished transactionId={} decision=NORMAL reason=no-rules", transaction.id().value());
            return FraudDetectionResult.normal(transaction);
        }

        List<RuleEvaluationResult> triggeredRules = DeterministicRuleEngine.withRules(rules).evaluate(transaction);

        if (triggeredRules.isEmpty()) {
            log.info("detection finished transactionId={} decision=NORMAL triggeredRules=0", transaction.id().value());
            return FraudDetectionResult.normal(transaction);
        }

        FraudAlert alert = alertFactory.createAlert(transaction, triggeredRules);
        log.info("alert created alertId={} transactionId={} decision={} severity={} triggeredRules={}", alert.alertId(), alert.transactionId(), alert.decision(), alert.severity(), alert.triggeredRules().size());

        alertPublisher.publish(alert);

        log.info("detection finished transactionId={} decision={} alertId={}", transaction.id().value(), alert.decision(), alert.alertId());

        return FraudDetectionResult.suspicious(transaction, alert);
    }
}
