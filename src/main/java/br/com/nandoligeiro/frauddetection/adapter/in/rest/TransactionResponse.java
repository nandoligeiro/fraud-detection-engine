package br.com.nandoligeiro.frauddetection.adapter.in.rest;

import br.com.nandoligeiro.frauddetection.application.port.in.IngestionStatus;

public record TransactionResponse(
        String transactionId,
        IngestionStatus status
) {
}
