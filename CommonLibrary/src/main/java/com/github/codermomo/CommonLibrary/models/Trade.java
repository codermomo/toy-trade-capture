package com.github.codermomo.CommonLibrary.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "trade")
@Table(name = "trade")
public class Trade implements Serializable {
    @Id
    @Column(name = "trade_id")
    private UUID tradeId;

    @Column(name = "trade_date_time")
    private LocalDateTime tradeDateTime;

    @Column(name = "instrument_id")
    private String instrumentId;
    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_type")
    private InstrumentType instrumentType;

    @Column(name = "quantity")
    private BigDecimal quantity; // > 0: Long; < 0: Short.
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "base_currency")
    private Currency baseCurrency;

    @Column(name = "source_id")
    private String sourceId; // strategy id
    @Column(name = "book_id")
    private String bookId;

    @Column(name = "counterparty_id")
    private String counterpartyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status")
    private TradeStatus tradeStatus;
}
