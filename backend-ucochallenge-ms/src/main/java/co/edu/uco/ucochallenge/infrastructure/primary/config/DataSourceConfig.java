package co.edu.uco.ucochallenge.infrastructure.primary.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import co.edu.uco.ucochallenge.domain.secret.port.SecretProviderPort;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);
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
        final HikariConfig config = new HikariConfig();

        final String url = properties.getUrl();
        if (StringUtils.hasText(url)) {
            config.setJdbcUrl(url);
        } else {
            log.warn("Property 'spring.datasource.url' is empty. Verify the active profile configuration.");
        }

        final String driverClassName = properties.getDriverClassName();
        if (StringUtils.hasText(driverClassName)) {
            config.setDriverClassName(driverClassName);
        } else {
            log.warn("Property 'spring.datasource.driver-class-name' is empty. Relying on JDBC auto-detection.");
        }

        final String username = secretProvider.getSecret(SECRET_DB_USERNAME);
        if (!StringUtils.hasText(username)) {
            log.warn("Secret '{}' resolved as empty. Database connections might fail.", SECRET_DB_USERNAME);
        }
        config.setUsername(username);

        final String password = secretProvider.getSecret(SECRET_DB_PASSWORD);
        if (!StringUtils.hasText(password)) {
            log.warn("Secret '{}' resolved as empty. Database connections might fail.", SECRET_DB_PASSWORD);
        }
        config.setPassword(password);

        final DataSourceProperties.Hikari hikari = properties.getHikari();
        if (hikari.getMaximumPoolSize() != null) {
            config.setMaximumPoolSize(hikari.getMaximumPoolSize());
        }
        if (hikari.getMinimumIdle() != null) {
            config.setMinimumIdle(hikari.getMinimumIdle());
        }
        if (hikari.getConnectionTimeout() != null) {
            config.setConnectionTimeout(hikari.getConnectionTimeout());
        }
        if (StringUtils.hasText(hikari.getPoolName())) {
            config.setPoolName(hikari.getPoolName());
        }

        return new HikariDataSource(config);
    }
}
