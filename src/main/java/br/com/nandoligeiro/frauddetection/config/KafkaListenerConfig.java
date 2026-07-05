package br.com.nandoligeiro.frauddetection.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "fraud.kafka.enabled", havingValue = "true")
public class KafkaListenerConfig {
}
