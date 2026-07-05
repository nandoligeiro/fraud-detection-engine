package br.com.nandoligeiro.frauddetection.application.port.in;

import br.com.nandoligeiro.frauddetection.domain.model.Transaction;

import java.util.Objects;

public record EvaluateTransactionCommand(Transaction transaction) {

    public EvaluateTransactionCommand {
        Objects.requireNonNull(transaction, "transaction must not be null");
    }

    public static EvaluateTransactionCommand of(Transaction transaction) {
        return new EvaluateTransactionCommand(transaction);
    }
}
