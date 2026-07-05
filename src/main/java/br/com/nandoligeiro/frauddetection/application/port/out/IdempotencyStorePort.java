package br.com.nandoligeiro.frauddetection.application.port.out;

import java.time.Duration;

public interface IdempotencyStorePort {

    boolean tryStartProcessing(String transactionId, Duration ttl);
}
