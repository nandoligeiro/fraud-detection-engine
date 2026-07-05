package br.com.nandoligeiro.frauddetection.adapter.out.memory;

import br.com.nandoligeiro.frauddetection.application.port.out.IdempotencyStorePort;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryIdempotencyStoreAdapter implements IdempotencyStorePort {

    private final Map<String, Instant> keys = new ConcurrentHashMap<>();

    @Override
    public boolean tryStartProcessing(String transactionId, Duration ttl) {
        cleanupExpiredKeys();
        Instant expiresAt = Instant.now().plus(ttl);
        return keys.putIfAbsent(transactionId, expiresAt) == null;
    }

    private void cleanupExpiredKeys() {
        Instant now = Instant.now();
        keys.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
