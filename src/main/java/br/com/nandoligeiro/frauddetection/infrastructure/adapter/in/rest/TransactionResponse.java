package br.com.nandoligeiro.frauddetection.infrastructure.adapter.in.rest;

import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestionStatus;

public record TransactionResponse(
        String transactionId,
        IngestionStatus status
) {
}
