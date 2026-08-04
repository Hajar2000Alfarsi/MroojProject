package com.example.mroojBE.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * spring-boot-starter-webmvc (the artifact this project uses instead of the
 * classic spring-boot-starter-web) does not auto-configure a Jackson
 * ObjectMapper bean the way spring-boot-starter-json normally does. This
 * provides one explicitly, guarded by @ConditionalOnMissingBean so it backs
 * off harmlessly if Jackson auto-config ever becomes active later.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}