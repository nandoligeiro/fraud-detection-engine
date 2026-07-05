package br.com.nandoligeiro.frauddetection.adapter.out.logging;

import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
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
