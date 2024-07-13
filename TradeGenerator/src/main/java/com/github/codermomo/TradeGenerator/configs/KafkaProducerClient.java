package com.github.codermomo.TradeGenerator.configs;

import com.github.codermomo.CommonLibrary.models.Trade;
import com.github.codermomo.CommonLibrary.models.TradeKafkaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaProducerClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerClient.class);

    @Value("${kafka.topics.trades.name}")
    private String kafkaTopicName;

    @Autowired
    private KafkaTemplate<String, TradeKafkaDto> kafkaTemplate;

    public CompletableFuture<SendResult<String, TradeKafkaDto>> asyncSendTradeMessage(TradeKafkaDto trade) {
        return kafkaTemplate.send(kafkaTopicName, trade);
    }

    public void sendTradeMessage(TradeKafkaDto trade) {
        CompletableFuture<SendResult<String, TradeKafkaDto>> future = asyncSendTradeMessage(trade);
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                LOGGER.trace("Sent message=[{}] with offset=[{}]", trade.toString(), result.getRecordMetadata().offset());
            } else {
                LOGGER.error("Failed to send message=[{}] with error: {}", trade.toString(), exception.getMessage());
            }
        });
    }
}
