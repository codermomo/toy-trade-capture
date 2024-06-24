package com.github.codermomo.ViewService.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Position {
    private final String bookId;
    private final String instrumentId;
    private final BigDecimal quantity;
}
