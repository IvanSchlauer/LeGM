package org.project.legm.bl;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 05.06.2024
 * Time: 11:07
 */
@Component
public class WebClientConfig {
    public WebClient getHighLoadWebClient() {
        return WebClient
                .builder()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(500 * 4048))
                        .build())
                .build();
    }
}
