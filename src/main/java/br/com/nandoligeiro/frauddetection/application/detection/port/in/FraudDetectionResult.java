package br.com.nandoligeiro.frauddetection.application.detection.port.in;

import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.model.FraudDecision;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;

import java.util.Optional;

public record FraudDetectionResult(
        String transactionId,
        FraudDecision decision,
        Optional<FraudAlert> alert
) {

    public static FraudDetectionResult normal(Transaction transaction) {
        return new FraudDetectionResult(transaction.id().value(), FraudDecision.NORMAL, Optional.empty());
    }

    public static FraudDetectionResult suspicious(Transaction transaction, FraudAlert alert) {
        return new FraudDetectionResult(transaction.id().value(), FraudDecision.SUSPICIOUS, Optional.of(alert));
    }

    public boolean hasAlert() {
        return alert.isPresent();
    }
}
