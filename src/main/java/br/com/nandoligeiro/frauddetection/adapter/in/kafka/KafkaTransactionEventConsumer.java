package br.com.nandoligeiro.frauddetection.adapter.in.kafka;

import br.com.nandoligeiro.frauddetection.adapter.kafka.TransactionEventMapper;
import br.com.nandoligeiro.frauddetection.adapter.kafka.TransactionEventPayload;
import br.com.nandoligeiro.frauddetection.application.port.in.DetectFraudUseCase;
import br.com.nandoligeiro.frauddetection.application.port.in.EvaluateTransactionCommand;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.kafka.enabled", havingValue = "true")
public class KafkaTransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionEventConsumer.class);

    private final DetectFraudUseCase detectFraudUseCase;
    private final TransactionEventMapper mapper;

    public KafkaTransactionEventConsumer(
            DetectFraudUseCase detectFraudUseCase,
            TransactionEventMapper mapper
    ) {
        this.detectFraudUseCase = detectFraudUseCase;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "${fraud.kafka.topics.transaction-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(TransactionEventPayload payload) {
        Transaction transaction = mapper.toDomain(payload);
        var result = detectFraudUseCase.detect(EvaluateTransactionCommand.of(transaction));

        log.info("transaction evaluated transactionId={} decision={} hasAlert={}",
                result.transactionId(),
                result.decision(),
                result.hasAlert()
        );
    }
}
