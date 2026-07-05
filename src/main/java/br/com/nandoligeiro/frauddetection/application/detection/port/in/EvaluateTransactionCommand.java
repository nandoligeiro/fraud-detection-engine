package br.com.nandoligeiro.frauddetection.application.detection.port.in;

import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;

public final class EvaluateTransactionCommand {

    private final Transaction transaction;

    private EvaluateTransactionCommand(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("transaction must not be null");
        }
        this.transaction = transaction;
    }

    public static EvaluateTransactionCommand of(Transaction transaction) {
        return new EvaluateTransactionCommand(transaction);
    }

    public Transaction transaction() {
        return transaction;
    }
}
