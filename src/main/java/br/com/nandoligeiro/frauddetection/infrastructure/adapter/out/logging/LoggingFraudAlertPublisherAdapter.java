package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.logging;

import br.com.nandoligeiro.frauddetection.application.detection.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.kafka.alert-publisher.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingFraudAlertPublisherAdapter implements FraudAlertPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingFraudAlertPublisherAdapter.class);

    @Override
    public void publish(FraudAlert alert) {
        log.info("fraud alert ready for async publication alertId={} transactionId={} severity={} triggeredRules={}",
                alert.alertId(),
                alert.transactionId(),
                alert.severity(),
                alert.triggeredRules().size()
        );
    }
}
