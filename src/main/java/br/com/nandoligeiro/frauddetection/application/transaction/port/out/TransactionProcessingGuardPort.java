package br.com.nandoligeiro.frauddetection.application.transaction.port.out;

import java.time.Duration;

public interface TransactionProcessingGuardPort {

    boolean acquire(String transactionId, Duration ttl);
}
