package br.com.nandoligeiro.frauddetection.infrastructure.adapter.in.kafka;

import br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka.FraudAlertEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.kafka.alert-consumer.enabled", havingValue = "true")
public class KafkaFraudAlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaFraudAlertConsumer.class);

    @KafkaListener(
            topics = "${fraud.kafka.topics.fraud-alerts}",
            groupId = "fraud-alert-notification-simulator",
            containerFactory = "fraudAlertKafkaListenerContainerFactory"
    )
    public void consume(FraudAlertEventPayload payload) {
        log.info(
                "fraud alert delivered to notification simulator eventId={} alertId={} transactionId={} accountId={} decision={} severity={} triggeredRules={}",
                payload.eventId(),
                payload.alertId(),
                payload.transactionId(),
                payload.accountId(),
                payload.decision(),
                payload.severity(),
                payload.triggeredRules().size()
        );
    }
}
