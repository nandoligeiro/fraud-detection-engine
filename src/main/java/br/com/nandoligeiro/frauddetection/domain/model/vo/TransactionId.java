package br.com.nandoligeiro.frauddetection.domain.model.vo;

import java.util.Objects;

public record TransactionId(String value) {

    public TransactionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("transactionId must not be blank");
        }
    }

    public static TransactionId of(String value) {
        return new TransactionId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
