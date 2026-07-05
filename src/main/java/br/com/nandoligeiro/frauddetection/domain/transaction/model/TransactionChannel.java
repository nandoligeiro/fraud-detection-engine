package br.com.nandoligeiro.frauddetection.domain.transaction.model;

public enum TransactionChannel {
    CARD_PRESENT,
    CARD_NOT_PRESENT,
    WALLET,
    ATM,
    PIX,
    UNKNOWN
}
