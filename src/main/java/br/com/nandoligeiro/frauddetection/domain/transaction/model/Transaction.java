package br.com.nandoligeiro.frauddetection.domain.transaction.model;

import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.TransactionId;

import java.time.Instant;
import java.util.Objects;

public final class Transaction {
    private final TransactionId id;
    private final AccountId accountId;
    private final CardId cardId;
    private final Money money;
    private final Merchant merchant;
    private final TransactionChannel channel;
    private final GeoLocation location;
    private final Instant occurredAt;

    private Transaction(TransactionId id, AccountId accountId, CardId cardId, Money money, Merchant merchant, TransactionChannel channel, GeoLocation location, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "transaction id must not be null");
        this.accountId = Objects.requireNonNull(accountId, "account id must not be null");
        this.cardId = Objects.requireNonNull(cardId, "card id must not be null");
        this.money = Objects.requireNonNull(money, "money must not be null");
        this.merchant = Objects.requireNonNull(merchant, "merchant must not be null");
        this.channel = Objects.requireNonNull(channel, "channel must not be null");
        this.location = Objects.requireNonNull(location, "location must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    public static Transaction create(TransactionId id, AccountId accountId, CardId cardId, Money money, Merchant merchant, TransactionChannel channel, GeoLocation location, Instant occurredAt) {
        return new Transaction(id, accountId, cardId, money, merchant, channel, location, occurredAt);
    }

    public boolean isHighAmount(Money threshold) {
        return money.isGreaterThanOrEqualTo(threshold);
    }

    public boolean isInternationalComparedTo(String homeCountry) {
        return location.isInternationalComparedTo(homeCountry);
    }

    public boolean isCardNotPresent() {
        return channel == TransactionChannel.CARD_NOT_PRESENT;
    }

    public boolean isFromMerchantCategory(String merchantCategoryCode) {
        return merchant.hasCategory(merchantCategoryCode);
    }

    public TransactionId id() { return id; }
    public AccountId accountId() { return accountId; }
    public CardId cardId() { return cardId; }
    public Money money() { return money; }
    public Merchant merchant() { return merchant; }
    public TransactionChannel channel() { return channel; }
    public GeoLocation location() { return location; }
    public Instant occurredAt() { return occurredAt; }
}
