package com.github.codermomo.TradeGenerator.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "generator")
public class InstrumentParamConfig {
    private Map<String, InstrumentDetails> instruments;

    @Getter
    @Setter
    public static class InstrumentDetails {
        private String instrumentType;
        private String baseCurrency;
        private List<String> counterpartyIds;
    }
}
