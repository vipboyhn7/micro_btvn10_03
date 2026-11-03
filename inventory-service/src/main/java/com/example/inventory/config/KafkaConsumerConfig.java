package com.example.inventory.config;

import com.example.inventory.model.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    public static final String GROUP_ID = "inventory-service-group";

    @Bean
    ConsumerFactory<String, OrderEvent> orderEventConsumerFactory(KafkaProperties properties) {
        JsonDeserializer<OrderEvent> valueDeserializer = new JsonDeserializer<>(OrderEvent.class);
        valueDeserializer.addTrustedPackages("com.example.inventory.model");
        valueDeserializer.setUseTypeHeaders(false);
        Map<String, Object> consumerProperties = new HashMap<>(properties.buildConsumerProperties());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProperties, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
