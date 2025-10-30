package co.edu.uco.ucochallenge.infrastructure.primary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Simple projection that binds the subset of the Spring datasource properties
 * that are required to bootstrap the custom {@link javax.sql.DataSource} bean.
 */
@ConfigurationProperties(prefix = "spring.datasource")
public record DataSourceProperties(String url, String driverClassName) {
}

