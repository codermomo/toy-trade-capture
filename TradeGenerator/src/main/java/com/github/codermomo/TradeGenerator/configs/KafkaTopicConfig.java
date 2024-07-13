package com.github.codermomo.TradeGenerator.configs;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    private static final long RETENTION_PERIOD_MS = 1000 * 3600; // 1 hour

    @Value("${kafka.topics.trades.name}")
    private String kafkaTopicName;
    @Value("${kafka.topics.trades.partitions}")
    private int partitions;
    @Value("${kafka.topics.trades.replicas}")
    private int replicas;
    @Value("${kafka.bootstrap-server.url}")
    private String bootstrapServerUrl;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl);
        return new KafkaAdmin(configs);
    }

    // https://docs.confluent.io/platform/current/installation/configuration/topic-configs.html
    @Bean
    public NewTopic tradesTopic() {
        return TopicBuilder.name(kafkaTopicName)
                .partitions(partitions)
                .replicas(replicas)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(RETENTION_PERIOD_MS))
                .build();
    }
}
