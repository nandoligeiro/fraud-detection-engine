package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.kafka;

import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka.TransactionEventMapper;
import br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka.TransactionEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.kafka.enabled", havingValue = "true")
public class KafkaTransactionEventPublisherAdapter implements TransactionEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionEventPublisherAdapter.class);

    private final KafkaTemplate<String, TransactionEventPayload> kafkaTemplate;
    private final TransactionEventMapper mapper;
    private final String topic;

    public KafkaTransactionEventPublisherAdapter(KafkaTemplate<String, TransactionEventPayload> kafkaTemplate, TransactionEventMapper mapper, @Value("${fraud.kafka.topics.transaction-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }

    @Override
    public void publish(Transaction transaction) {
        TransactionEventPayload payload = mapper.toPayload(transaction);
        kafkaTemplate.send(topic, transaction.accountId().value(), payload);
        log.info("transaction event published topic={} eventId={} transactionId={} accountId={} channel={} country={}", topic, payload.eventId(), payload.transactionId(), payload.accountId(), payload.channel(), payload.country());
    }
}
