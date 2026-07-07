package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.memory;

import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionProcessingGuardPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "fraud.idempotency.provider", havingValue = "memory", matchIfMissing = true)
public class InMemoryIdempotencyStoreAdapter implements TransactionProcessingGuardPort {

    private final Map<String, Instant> keys = new ConcurrentHashMap<>();

    @Override
    public boolean acquire(String transactionId, Duration ttl) {
        cleanupExpiredKeys();
        Instant expiresAt = Instant.now().plus(ttl);
        return keys.putIfAbsent(transactionId, expiresAt) == null;
    }

    private void cleanupExpiredKeys() {
        Instant now = Instant.now();
        keys.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
