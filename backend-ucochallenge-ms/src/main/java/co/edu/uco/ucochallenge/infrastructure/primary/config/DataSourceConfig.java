package co.edu.uco.ucochallenge.infrastructure.primary.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import co.edu.uco.ucochallenge.domain.secret.port.SecretProviderPort;

@Configuration
public class DataSourceConfig {

    private static final String SECRET_DB_USERNAME = "db-username";
    private static final String SECRET_DB_PASSWORD = "db-password";

    private final SecretProviderPort secretProvider;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    public DataSourceConfig(final SecretProviderPort secretProvider) {
        this.secretProvider = secretProvider;
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
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
