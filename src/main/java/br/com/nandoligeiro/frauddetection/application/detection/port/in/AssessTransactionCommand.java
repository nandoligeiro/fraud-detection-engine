package br.com.nandoligeiro.frauddetection.application.detection.port.in;

import br.com.nandoligeiro.frauddetection.domain.model.Transaction;

import java.util.Objects;

public record AssessTransactionCommand(Transaction transaction) {

    public AssessTransactionCommand {
        Objects.requireNonNull(transaction, "transaction must not be null");
    }

    public static AssessTransactionCommand of(Transaction transaction) {
        return new AssessTransactionCommand(transaction);
    }
}
