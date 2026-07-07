package br.com.nandoligeiro.frauddetection.application.transaction.service;

import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.TransactionIngestionUseCase;
import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionProcessingGuardPort;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.TransactionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TransactionIngestionService implements TransactionIngestionUseCase {

    private static final Logger log = LoggerFactory.getLogger(TransactionIngestionService.class);
    private static final Duration TTL = Duration.ofMinutes(10);

    private final TransactionProcessingGuardPort guard;
    private final TransactionEventPublisherPort publisher;

    public TransactionIngestionService(TransactionProcessingGuardPort guard, TransactionEventPublisherPort publisher) {
        this.guard = guard;
        this.publisher = publisher;
    }

    @Override
    public IngestionResult ingest(IngestTransactionCommand command) {
        log.info("transaction ingestion started transactionId={} accountId={} amount={} currency={} channel={} country={}", command.transactionId(), command.accountId(), command.amount(), command.currency(), command.channel(), command.country());

        if (!guard.acquire(command.transactionId(), TTL)) {
            log.info("transaction ingestion duplicated transactionId={} ttlSeconds={}", command.transactionId(), TTL.toSeconds());
            return IngestionResult.duplicated(command.transactionId());
        }

        Transaction transaction = toTransaction(command);
        publisher.publish(transaction);

        log.info("transaction accepted for async processing transactionId={} accountId={} channel={} country={}", transaction.id().value(), transaction.accountId().value(), transaction.channel(), transaction.location().country());

        return IngestionResult.accepted(transaction.id().value());
    }

    private Transaction toTransaction(IngestTransactionCommand command) {
        return Transaction.create(
                TransactionId.of(command.transactionId()),
                AccountId.of(command.accountId()),
                CardId.of(command.cardId()),
                Money.of(command.amount(), command.currency()),
                Merchant.of(command.merchantId(), command.merchantCategoryCode()),
                command.channel(),
                GeoLocation.of(command.country(), command.city(), command.latitude(), command.longitude()),
                command.occurredAt()
        );
    }
}
