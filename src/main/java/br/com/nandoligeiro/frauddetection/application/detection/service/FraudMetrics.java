package br.com.nandoligeiro.frauddetection.application.detection.service;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FraudMetrics {

    private final MeterRegistry registry;

    public FraudMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void normal(String reason) {
        registry.counter("fraud.decisions", "decision", "normal", "reason", reason).increment();
    }

    public void suspicious(FraudSeverity severity) {
        registry.counter("fraud.decisions", "decision", "suspicious", "severity", severity.name()).increment();
    }

    public void alert(FraudSeverity severity) {
        registry.counter("fraud.alerts", "severity", severity.name()).increment();
    }

    public void rules(int count) {
        registry.summary("fraud.triggered.rules").record(count);
    }

    public void auditSuccess() {
        registry.counter("fraud.audit", "result", "success").increment();
    }

    public void auditFailure() {
        registry.counter("fraud.audit", "result", "failure").increment();
    }
}
