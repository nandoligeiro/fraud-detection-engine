package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.redis;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class RedisProcessingGuardAdapterTest {

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @Test
    void shouldAcquireOnlyOnceForSameTransactionId() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        try {
            StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
            template.afterPropertiesSet();
            RedisProcessingGuardAdapter adapter = new RedisProcessingGuardAdapter(template);

            boolean first = adapter.acquire("tx-redis-test-001", Duration.ofMinutes(10));
            boolean second = adapter.acquire("tx-redis-test-001", Duration.ofMinutes(10));

            assertThat(first).isTrue();
            assertThat(second).isFalse();
        } finally {
            connectionFactory.destroy();
        }
    }
}
