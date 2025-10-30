package co.edu.uco.parametersservice.config;

import co.edu.uco.parametersservice.catalog.ReactiveParameterCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveCatalogConfig {

    @Bean
    public ReactiveParameterCatalog reactiveParameterCatalog() {
        return new ReactiveParameterCatalog();
    }
}
