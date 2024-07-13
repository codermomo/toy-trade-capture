package com.github.codermomo.CommonLibrary.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeKafkaDto {
    private UUID tradeId;

    private String tradeDateTimeStr; // e.g. '2011-12-03T10:15:30'

    private String instrumentId;
    private InstrumentType instrumentType;

    private BigDecimal quantity; // > 0: Long; < 0: Short.
    private BigDecimal unitPrice;
    private Currency baseCurrency;

    private String sourceId; // strategy id
    private String bookId;

    private String counterpartyId;

    private TradeStatus tradeStatus;

    public static TradeKafkaDto convertToDto(Trade trade) {
        return TradeKafkaDto.builder()
                .tradeId(trade.getTradeId())
                .tradeDateTimeStr(trade.getTradeDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .instrumentId(trade.getInstrumentId())
                .instrumentType(trade.getInstrumentType())
                .quantity(trade.getQuantity())
                .unitPrice(trade.getUnitPrice())
                .baseCurrency(trade.getBaseCurrency())
                .sourceId(trade.getSourceId())
                .bookId(trade.getBookId())
                .counterpartyId(trade.getCounterpartyId())
                .tradeStatus(trade.getTradeStatus())
                .build();
    }
}
