package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.redis;

import br.com.nandoligeiro.frauddetection.application.transaction.port.out.TransactionProcessingGuardPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConditionalOnProperty(name = "fraud.idempotency.provider", havingValue = "redis")
public class RedisProcessingGuardAdapter implements TransactionProcessingGuardPort {

    private static final String KEY_PREFIX = "fraud:transaction:";

    private final StringRedisTemplate redisTemplate;

    public RedisProcessingGuardAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean acquire(String transactionId, Duration ttl) {
        String key = KEY_PREFIX + transactionId;
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);
        return Boolean.TRUE.equals(acquired);
    }
}
