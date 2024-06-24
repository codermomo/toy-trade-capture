package com.github.codermomo.TradeGenerator.services;

import com.github.codermomo.CommonLibrary.models.Trade;
import jakarta.jms.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class TradePublisher {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private TradeGenerator tradeGenerator;

    // Hard-coded
    private final Destination destination = new ActiveMQQueue("trades");
    private final long publishInterval = 2000;

    private void publishRandomTrade(Destination destination) {
        Trade trade = tradeGenerator.generateRandomTrade();

        assert trade != null;
        jmsTemplate.convertAndSend(destination, trade);
    }

    @Scheduled(fixedRate = publishInterval)
    public void scheduledPublishRandomTrade() {
        publishRandomTrade(destination);
    }
}
