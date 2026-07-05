package br.com.nandoligeiro.frauddetection.application.transaction.service;

import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.TransactionIngestionUseCase;
import br.com.nandoligeiro.frauddetection.application.transaction.port.out.IdempotencyStorePort;
import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.TransactionId;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TransactionIngestionService implements TransactionIngestionUseCase {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final IdempotencyStorePort idempotencyStore;
    private final TransactionEventPublisherPort transactionEventPublisher;

    public TransactionIngestionService(IdempotencyStorePort idempotencyStore, TransactionEventPublisherPort transactionEventPublisher) {
        this.idempotencyStore = idempotencyStore;
        this.transactionEventPublisher = transactionEventPublisher;
    }

    @Override
    public IngestionResult execute(IngestTransactionCommand command) {
        boolean firstProcessing = idempotencyStore.tryStartProcessing(command.transactionId(), TTL);
        if (!firstProcessing) {
            return IngestionResult.duplicated(command.transactionId());
        }
        Transaction transaction = Transaction.create(
                TransactionId.of(command.transactionId()),
                AccountId.of(command.accountId()),
                CardId.of(command.cardId()),
                Money.of(command.amount(), command.currency()),
                Merchant.of(command.merchantId(), command.merchantCategoryCode()),
                command.channel(),
                GeoLocation.of(command.country(), command.city(), command.latitude(), command.longitude()),
                command.occurredAt()
        );
        transactionEventPublisher.publish(transaction);
        return IngestionResult.accepted(transaction.id().value());
    }
}
