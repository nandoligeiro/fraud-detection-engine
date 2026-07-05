package br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka;

import br.com.nandoligeiro.frauddetection.domain.model.TransactionChannel;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionEventPayload(
        String eventId,
        String eventType,
        String transactionId,
        String accountId,
        String cardId,
        BigDecimal amount,
        String currency,
        String merchantId,
        String merchantCategoryCode,
        TransactionChannel channel,
        String country,
        String city,
        Double latitude,
        Double longitude,
        Instant occurredAt,
        Instant publishedAt
) {
}
