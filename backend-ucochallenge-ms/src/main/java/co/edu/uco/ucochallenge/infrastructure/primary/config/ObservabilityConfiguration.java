package co.edu.uco.ucochallenge.infrastructure.primary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationPredicate;

@Configuration
public class ObservabilityConfiguration {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(
            @Value("${spring.application.name:uco-challenge}") final String appName,
            @Value("${uco-challenge.telemetry.region:local}") final String region) {
        return registry -> registry.config()
                .commonTags("application", appName, "region", region);
    }

    @Bean
    MeterFilter commonTagsMeterFilter(@Value("${spring.application.name:uco-challenge}") final String appName) {
        return MeterFilter.commonTags("application", appName);
    }

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> observationRegistryCustomizer(
            @Value("${spring.application.name:uco-challenge}") final String appName) {
        return registry -> registry.observationConfig()
                .observationPredicate(observationPredicate())
                .lowCardinalityKeyValue("application", appName);
    }

    private ObservationPredicate observationPredicate() {
        return (name, context) -> true;
    }
}
