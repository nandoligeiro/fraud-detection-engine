package br.com.nandoligeiro.frauddetection.application.service;

import br.com.nandoligeiro.frauddetection.application.port.in.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.port.in.IngestTransactionUseCase;
import br.com.nandoligeiro.frauddetection.application.port.in.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.port.out.IdempotencyStorePort;
import br.com.nandoligeiro.frauddetection.application.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.model.vo.TransactionId;
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

        Transaction transaction = toTransaction(command);
        transactionEventPublisher.publish(transaction);
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
