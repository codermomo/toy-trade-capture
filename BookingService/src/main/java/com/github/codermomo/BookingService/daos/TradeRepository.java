package com.github.codermomo.BookingService.daos;

import com.github.codermomo.CommonLibrary.models.Trade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TradeRepository extends CrudRepository<Trade, UUID> {
    Trade findByTradeId(UUID uuid);
    List<Trade> findByInstrumentId(String instrumentId);
}
