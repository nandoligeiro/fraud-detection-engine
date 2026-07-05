package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.kafka;

import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka.FraudAlertEventMapper;
import br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka.FraudAlertEventPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.kafka.alert-publisher.enabled", havingValue = "true")
public class KafkaFraudAlertPublisherAdapter implements FraudAlertPublisherPort {

    private final KafkaTemplate<String, FraudAlertEventPayload> kafkaTemplate;
    private final FraudAlertEventMapper mapper;
    private final String topic;

    public KafkaFraudAlertPublisherAdapter(
            KafkaTemplate<String, FraudAlertEventPayload> kafkaTemplate,
            FraudAlertEventMapper mapper,
            @Value("${fraud.kafka.topics.fraud-alerts}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }

    @Override
    public void publish(FraudAlert alert) {
        FraudAlertEventPayload payload = mapper.toPayload(alert);
        kafkaTemplate.send(topic, alert.transactionId(), payload);
    }
}
