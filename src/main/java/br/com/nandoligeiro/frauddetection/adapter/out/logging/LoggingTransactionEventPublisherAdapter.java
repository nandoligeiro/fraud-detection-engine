package br.com.nandoligeiro.frauddetection.adapter.out.logging;

import br.com.nandoligeiro.frauddetection.application.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "fraud.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingTransactionEventPublisherAdapter implements TransactionEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingTransactionEventPublisherAdapter.class);

    @Override
    public void publish(Transaction transaction) {
        log.info("transaction accepted for async processing transactionId={} accountId={} amount={} channel={}",
                transaction.id().value(),
                transaction.accountId().value(),
                transaction.money(),
                transaction.channel()
        );
    }
}
