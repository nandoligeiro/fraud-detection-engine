package br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka;

import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.TransactionId;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
public class TransactionEventMapper {

    private static final String EVENT_TYPE = "TRANSACTION_RECEIVED";

    private final Clock clock;

    public TransactionEventMapper(Clock clock) {
        this.clock = clock;
    }

    public TransactionEventPayload toPayload(Transaction transaction) {
        return new TransactionEventPayload(
                "evt-" + UUID.randomUUID(),
                EVENT_TYPE,
                transaction.id().value(),
                transaction.accountId().value(),
                transaction.cardId().value(),
                transaction.money().amount(),
                transaction.money().currency().getCurrencyCode(),
                transaction.merchant().merchantId(),
                transaction.merchant().merchantCategoryCode(),
                transaction.channel(),
                transaction.location().country(),
                transaction.location().city(),
                transaction.location().latitude(),
                transaction.location().longitude(),
                transaction.occurredAt(),
                Instant.now(clock)
        );
    }

    public Transaction toDomain(TransactionEventPayload payload) {
        return Transaction.create(
                TransactionId.of(payload.transactionId()),
                AccountId.of(payload.accountId()),
                CardId.of(payload.cardId()),
                Money.of(payload.amount(), payload.currency()),
                Merchant.of(payload.merchantId(), payload.merchantCategoryCode()),
                payload.channel(),
                GeoLocation.of(payload.country(), payload.city(), payload.latitude(), payload.longitude()),
                payload.occurredAt()
        );
    }
}
