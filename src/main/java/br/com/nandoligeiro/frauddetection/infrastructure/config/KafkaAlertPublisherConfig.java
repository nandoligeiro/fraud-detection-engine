package br.com.nandoligeiro.frauddetection.infrastructure.config;

import br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka.FraudAlertEventPayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "fraud.kafka.alert-publisher.enabled", havingValue = "true")
public class KafkaAlertPublisherConfig {

    @Bean
    ProducerFactory<String, FraudAlertEventPayload> fraudAlertProducerFactory(
            @Value("${fraud.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    KafkaTemplate<String, FraudAlertEventPayload> fraudAlertKafkaTemplate(
            ProducerFactory<String, FraudAlertEventPayload> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @ConditionalOnProperty(name = "fraud.kafka.alert-consumer.enabled", havingValue = "true")
    ConsumerFactory<String, FraudAlertEventPayload> fraudAlertConsumerFactory(
            @Value("${fraud.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<FraudAlertEventPayload> valueDeserializer = new JsonDeserializer<>(FraudAlertEventPayload.class);
        valueDeserializer.addTrustedPackages("br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka");

        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    @ConditionalOnProperty(name = "fraud.kafka.alert-consumer.enabled", havingValue = "true")
    ConcurrentKafkaListenerContainerFactory<String, FraudAlertEventPayload> fraudAlertKafkaListenerContainerFactory(
            ConsumerFactory<String, FraudAlertEventPayload> fraudAlertConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, FraudAlertEventPayload> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(fraudAlertConsumerFactory);
        return factory;
    }
}
