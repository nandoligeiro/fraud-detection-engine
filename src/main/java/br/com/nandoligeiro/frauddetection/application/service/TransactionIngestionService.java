package br.com.nandoligeiro.frauddetection.application.service;

import br.com.nandoligeiro.frauddetection.application.port.in.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.port.in.IngestTransactionUseCase;
import br.com.nandoligeiro.frauddetection.application.port.in.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.port.out.IdempotencyStorePort;
import br.com.nandoligeiro.frauddetection.application.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TransactionIngestionService implements IngestTransactionUseCase {

    private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(10);

    private final IdempotencyStorePort idempotencyStore;
    private final TransactionEventPublisherPort transactionEventPublisher;

    public TransactionIngestionService(
            IdempotencyStorePort idempotencyStore,
            TransactionEventPublisherPort transactionEventPublisher
    ) {
        this.idempotencyStore = idempotencyStore;
        this.transactionEventPublisher = transactionEventPublisher;
    }

    @Override
    public IngestionResult ingest(IngestTransactionCommand command) {
        boolean firstProcessing = idempotencyStore.tryStartProcessing(command.transactionId(), IDEMPOTENCY_TTL);

        if (!firstProcessing) {
            return IngestionResult.duplicated(command.transactionId());
        }

        transactionEventPublisher.publish(toTransaction(command));
        return IngestionResult.accepted(command.transactionId());
    }

    private Transaction toTransaction(IngestTransactionCommand command) {
        return new Transaction(
                command.transactionId(),
                command.accountId(),
                command.cardId(),
                command.amount(),
                command.currency(),
                command.merchantId(),
                command.merchantCategoryCode(),
                command.channel(),
                command.country(),
                command.city(),
                command.latitude(),
                command.longitude(),
                command.occurredAt()
        );
    }
}
