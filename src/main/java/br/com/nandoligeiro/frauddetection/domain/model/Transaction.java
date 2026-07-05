package br.com.nandoligeiro.frauddetection.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
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
        Instant occurredAt
) {
}
