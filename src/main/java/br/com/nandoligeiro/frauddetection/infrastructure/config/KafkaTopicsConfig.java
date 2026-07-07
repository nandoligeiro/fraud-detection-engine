package br.com.nandoligeiro.frauddetection.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "fraud.kafka.enabled", havingValue = "true")
public class KafkaTopicsConfig {

    @Bean
    NewTopic transactionEventsTopic(@Value("${fraud.kafka.topics.transaction-events}") String topic) {
        return TopicBuilder.name(topic).partitions(12).replicas(1).build();
    }

    @Bean
    NewTopic fraudAlertsTopic(@Value("${fraud.kafka.topics.fraud-alerts}") String topic) {
        return TopicBuilder.name(topic).partitions(12).replicas(1).build();
    }

    @Bean
    NewTopic ruleUpdatesTopic(@Value("${fraud.kafka.topics.rule-updates}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic fraudDlqTopic(@Value("${fraud.kafka.topics.dlq}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }
}
