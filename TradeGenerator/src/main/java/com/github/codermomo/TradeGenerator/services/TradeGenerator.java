package com.github.codermomo.TradeGenerator.services;

import com.github.codermomo.CommonLibrary.models.Trade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TradeGenerator {
    // Hard-coded
    private final Random random = new Random();
    private final List<String> sourceIds = Arrays.asList("strat1", "strat2", "strat3");
    private final List<String> instrumentIds = Arrays.asList("AAPL", "NVDA", "BRK/B", "TQQQ");
    private final List<String> bookIds = Arrays.asList("book1", "book2", "book3", "book4");

    public Trade generateRandomTrade() {
        return Trade.builder()
                .tradeId(UUID.randomUUID())
                .instrumentId(instrumentIds.get(random.nextInt(instrumentIds.size())))
                .sourceId(sourceIds.get(random.nextInt(sourceIds.size())))
                .bookId(bookIds.get(random.nextInt(bookIds.size())))
                .quantity(BigDecimal.valueOf(random.nextInt(-100, 100)))
                .unitPrice(BigDecimal.valueOf(((double) random.nextInt(100, 10000)) / 10))
                .build();
    }
}
