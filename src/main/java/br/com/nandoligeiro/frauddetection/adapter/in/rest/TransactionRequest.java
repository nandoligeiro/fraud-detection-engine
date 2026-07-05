package br.com.nandoligeiro.frauddetection.adapter.in.rest;

import br.com.nandoligeiro.frauddetection.domain.model.TransactionChannel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest(
        @NotBlank String transactionId,
        @NotBlank String accountId,
        @NotBlank String cardId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String merchantId,
        @NotBlank String merchantCategoryCode,
        @NotNull TransactionChannel channel,
        @NotBlank String country,
        String city,
        Double latitude,
        Double longitude,
        @NotNull Instant occurredAt
) {
}
