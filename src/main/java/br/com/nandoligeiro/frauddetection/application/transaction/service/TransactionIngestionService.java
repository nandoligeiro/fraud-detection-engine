package br.com.nandoligeiro.frauddetection.application.transaction.service;

import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.TransactionIngestionUseCase;
import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionEventPublisherPort;
import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionProcessingGuardPort;
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
public class TransactionIngestionService implements TransactionIngestionUseCase {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final TransactionProcessingGuardPort guard;
    private final TransactionEventPublisherPort publisher;

    public TransactionIngestionService(TransactionProcessingGuardPort guard, TransactionEventPublisherPort publisher) {
        this.guard = guard;
        this.publisher = publisher;
    }

    @Override
    public IngestionResult ingest(IngestTransactionCommand command) {
        if (!guard.acquire(command.transactionId(), TTL)) {
            return IngestionResult.duplicated(command.transactionId());
        }
        Transaction transaction = toTransaction(command);
        publisher.publish(transaction);
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
