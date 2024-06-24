package com.github.codermomo.BookingService.services;

import com.github.codermomo.CommonLibrary.models.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class TradeConsumer {

    @Autowired
    private BookingService bookingService;

    // Hard-coded
    private final String destinationQueueName = "trades";
    private final String containerFactoryName = "customJmsFactory";

    @JmsListener(destination = destinationQueueName, containerFactory = containerFactoryName)
    public void consumeTrade(Trade trade) {
        System.out.printf("Received %s%n", trade.toString());
        bookingService.save(trade);
        System.out.println(bookingService.retrieveAllTrades().toString());
    }
}
