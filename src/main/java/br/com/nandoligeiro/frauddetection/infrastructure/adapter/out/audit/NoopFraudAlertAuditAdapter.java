package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.audit;

import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertAuditPort;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.audit.enabled", havingValue = "false", matchIfMissing = true)
public class NoopFraudAlertAuditAdapter implements FraudAlertAuditPort {

    @Override
    public void save(FraudAlert alert) {
        // Audit persistence disabled for local and MVP execution.
    }
}
