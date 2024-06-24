package com.github.codermomo.CommonLibrary.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Trade")
public class Trade implements Serializable {
    @Id
    private UUID tradeId;
    private String sourceId;
    private String instrumentId;
    private String bookId;
    private BigDecimal quantity; // > 0: Long; < 0: Short.
    private BigDecimal unitPrice;
    private String baseCurrency;
}
