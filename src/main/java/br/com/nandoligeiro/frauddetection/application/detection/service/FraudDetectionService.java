package br.com.nandoligeiro.frauddetection.application.detection.service;

import br.com.nandoligeiro.frauddetection.application.detection.port.in.DetectFraudUseCase;
import br.com.nandoligeiro.frauddetection.application.detection.port.in.EvaluateTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.detection.port.in.FraudDetectionResult;
import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertAuditPort;
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
    private final FraudAlertAuditPort alertAudit;
    private final FraudMetrics metrics;
    private final FraudAlertFactory alertFactory;

    public FraudDetectionService(RuleProviderPort ruleProvider, FraudAlertPublisherPort alertPublisher, FraudAlertAuditPort alertAudit, FraudMetrics metrics, Clock clock) {
        this.ruleProvider = ruleProvider;
        this.alertPublisher = alertPublisher;
        this.alertAudit = alertAudit;
        this.metrics = metrics;
        this.alertFactory = new FraudAlertFactory(clock);
    }

    @Override
    public FraudDetectionResult detect(EvaluateTransactionCommand command) {
        Transaction transaction = command.transaction();
        List<FraudRule> rules = ruleProvider.loadRules();

        log.info("detection started transactionId={} accountId={} rulesLoaded={}", transaction.id().value(), transaction.accountId().value(), rules.size());

        if (rules.isEmpty()) {
            metrics.normal("no-rules");
            log.info("detection finished transactionId={} decision=NORMAL reason=no-rules", transaction.id().value());
            return FraudDetectionResult.normal(transaction);
        }

        List<RuleEvaluationResult> triggeredRules = DeterministicRuleEngine.withRules(rules).evaluate(transaction);

        if (triggeredRules.isEmpty()) {
            metrics.normal("no-triggered-rules");
            log.info("detection finished transactionId={} decision=NORMAL triggeredRules=0", transaction.id().value());
            return FraudDetectionResult.normal(transaction);
        }

        FraudAlert alert = alertFactory.createAlert(transaction, triggeredRules);
        metrics.suspicious(alert.severity());
        metrics.alert(alert.severity());
        metrics.rules(triggeredRules.size());
        log.info("alert created alertId={} transactionId={} decision={} severity={} triggeredRules={}", alert.alertId(), alert.transactionId(), alert.decision(), alert.severity(), alert.triggeredRules().size());

        saveAudit(alert);
        alertPublisher.publish(alert);

        log.info("detection finished transactionId={} decision={} alertId={}", transaction.id().value(), alert.decision(), alert.alertId());

        return FraudDetectionResult.suspicious(transaction, alert);
    }

    private void saveAudit(FraudAlert alert) {
        try {
            alertAudit.save(alert);
            metrics.auditSuccess();
            log.info("alert audit persisted alertId={} transactionId={}", alert.alertId(), alert.transactionId());
        } catch (Exception exception) {
            metrics.auditFailure();
            log.warn("alert audit failed alertId={} transactionId={}", alert.alertId(), alert.transactionId(), exception);
        }
    }
}
