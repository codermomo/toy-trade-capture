package com.github.codermomo.TradeGenerator.services;

import com.github.codermomo.CommonLibrary.models.Trade;
import com.github.codermomo.CommonLibrary.models.TradeKafkaDto;
import com.github.codermomo.TradeGenerator.configs.KafkaProducerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class TradePublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradePublisher.class);

    @Autowired
    private KafkaProducerClient kafkaProducerClient;
    @Autowired
    private TradeGenerator tradeGenerator;

    private void publishRandomTrade() {
        Trade trade = tradeGenerator.generateRandomTrade();

        if (trade == null) {
            LOGGER.error("Generated trade should not be null. Terminating this application.");
            System.exit(1);
        }
        kafkaProducerClient.sendTradeMessage(TradeKafkaDto.convertToDto(trade));
    }

    @Scheduled(fixedRateString = "${publisher.publish-interval-ms}")
    public void scheduledPublishRandomTrade() {
        publishRandomTrade();
    }
}
