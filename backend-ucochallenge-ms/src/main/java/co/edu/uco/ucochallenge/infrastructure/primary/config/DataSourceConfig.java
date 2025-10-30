package co.edu.uco.ucochallenge.infrastructure.primary.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.Assert;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import co.edu.uco.ucochallenge.domain.secret.port.SecretProviderPort;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {

    private static final String SECRET_DB_USERNAME = "db-username";
    private static final String SECRET_DB_PASSWORD = "db-password";

    private final SecretProviderPort secretProvider;
    private final DataSourceProperties properties;

    public DataSourceConfig(final SecretProviderPort secretProvider, final DataSourceProperties properties) {
        this.secretProvider = secretProvider;
        this.properties = properties;
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        final String url = properties.url();
        final String driverClassName = properties.driverClassName();

        Assert.hasText(url, "Property 'spring.datasource.url' must not be empty");
        Assert.hasText(driverClassName, "Property 'spring.datasource.driver-class-name' must not be empty");

        config.setJdbcUrl(url);
        config.setUsername(secretProvider.getSecret(SECRET_DB_USERNAME));
        config.setPassword(secretProvider.getSecret(SECRET_DB_PASSWORD));
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setPoolName("UcoHikariPool");

        return new HikariDataSource(config);
    }
}
