package com.github.codermomo.BookingService.services;

import com.github.codermomo.BookingService.daos.TradeRepository;
import com.github.codermomo.CommonLibrary.models.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private TradeRepository tradeRepository;

    public void save(Trade trade) {
        tradeRepository.save(trade);
    }

    public List<Trade> retrieveAllTrades() {
        List<Trade> trades = new ArrayList<>();
        Iterator<Trade> tradeIterable = tradeRepository.findAll().iterator();
        while (tradeIterable.hasNext()) {
            trades.add(tradeIterable.next());
        }
        return trades;
    }
}
