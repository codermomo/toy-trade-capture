package com.github.codermomo.TradeGenerator.services;

import com.github.codermomo.CommonLibrary.models.Currency;
import com.github.codermomo.CommonLibrary.models.InstrumentType;
import com.github.codermomo.CommonLibrary.models.Trade;
import com.github.codermomo.CommonLibrary.models.TradeStatus;
import com.github.codermomo.TradeGenerator.configs.GeneratorParamConfig;
import com.github.codermomo.TradeGenerator.configs.InstrumentParamConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TradeGenerator {

    @Autowired
    public TradeGenerator(GeneratorParamConfig generatorParamConfig, InstrumentParamConfig instrumentParamConfig) {
        this.random = generatorParamConfig.getRandom();
        this.sourceIds = generatorParamConfig.getSourceIds();
        this.instrumentIds = generatorParamConfig.getInstrumentIds();
        this.bookIds = generatorParamConfig.getBookIds();

        this.minQuantity = generatorParamConfig.getMinQuantity();
        this.maxQuantity = generatorParamConfig.getMaxQuantity();
        this.minPrice = generatorParamConfig.getMinPrice();
        this.maxPrice = generatorParamConfig.getMaxPrice();

        this.tradeStatus = generatorParamConfig.getTradeStatus();

        this.instrumentDetailsMap = instrumentParamConfig.getInstruments();
    }

    private final Random random;
    private final List<String> sourceIds;
    private final List<String> instrumentIds;
    private final List<String> bookIds;

    private final int minQuantity;
    private final int maxQuantity;
    private final int minPrice;
    private final int maxPrice;

    private final String tradeStatus;

    private final Map<String, InstrumentParamConfig.InstrumentDetails> instrumentDetailsMap;

    public Trade generateRandomTrade() {
        String instrumentId = instrumentIds.get(random.nextInt(instrumentIds.size()));
        InstrumentParamConfig.InstrumentDetails instrumentDetails = instrumentDetailsMap.get(instrumentId);
        List<String> counterpartyIds = instrumentDetails.getCounterpartyIds();

        return Trade.builder()
                .tradeId(UUID.randomUUID())
                .tradeDateTime(LocalDateTime.now())
                .instrumentId(instrumentId)
                .instrumentType(InstrumentType.valueOf(instrumentDetails.getInstrumentType()))
                .quantity(BigDecimal.valueOf(random.nextInt(minQuantity, maxQuantity)))
                .unitPrice(BigDecimal.valueOf((double) random.nextInt(minPrice, maxPrice)))
                .baseCurrency(Currency.valueOf(instrumentDetails.getBaseCurrency()))
                .sourceId(sourceIds.get(random.nextInt(sourceIds.size())))
                .bookId(bookIds.get(random.nextInt(bookIds.size())))
                .counterpartyId(counterpartyIds.get(random.nextInt(counterpartyIds.size())))
                .tradeStatus(TradeStatus.valueOf(tradeStatus))
                .build();
    }
}
