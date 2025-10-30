package co.edu.uco.messageservice.config;

import co.edu.uco.messageservice.catalog.ReactiveMessageCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the reactive catalog bean so it can be injected wherever needed.
 */
@Configuration
public class ReactiveCatalogConfig {

    @Bean
    public ReactiveMessageCatalog reactiveMessageCatalog() {
        return new ReactiveMessageCatalog();
    }
}
