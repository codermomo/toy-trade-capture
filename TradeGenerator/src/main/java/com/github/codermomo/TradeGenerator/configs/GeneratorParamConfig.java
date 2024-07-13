package com.github.codermomo.TradeGenerator.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "generator")
public class GeneratorParamConfig {
    private final Random random = new Random();

    private List<String> sourceIds;
    private List<String> bookIds;
    private List<String> instrumentIds;

    private int minQuantity;
    private int maxQuantity;
    private int minPrice;
    private int maxPrice;

    private String tradeStatus;
}
