package com.github.codermomo.BookingService.configs;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableJms
public class JmsTemplateConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    // Hard-coded
    private final List<String> trustedPackages = Arrays.asList(
            "com.github.codermomo.CommonLibrary", "java.math", "java.util");

    // https://activemq.apache.org/components/classic/documentation/objectmessage
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setTrustedPackages(trustedPackages);
        connectionFactory.setBrokerURL(brokerUrl);
        return connectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory<?> customJmsFactory(ActiveMQConnectionFactory activeMQConnectionFactory,
                                                     DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        configurer.configure(factory, activeMQConnectionFactory);

        return factory;
    }
}
