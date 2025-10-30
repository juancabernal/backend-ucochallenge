package co.edu.uco.ucochallenge.crosscuting.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import co.edu.uco.ucochallenge.crosscuting.secret.SecretProvider;

@Configuration
public class DataSourceConfig {

    private static final String SECRET_DB_URL = "spring.datasource.url";
    private static final String SECRET_DB_USERNAME = "spring.datasource.username";
    private static final String SECRET_DB_PASSWORD = "spring.datasource.password";
    private static final String SECRET_DB_DRIVER = "spring.datasource.driver-class-name";

    private final SecretProvider secrets;

    public DataSourceConfig(final SecretProvider secrets) {
        this.secrets = secrets;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        final HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl(secrets.get(SECRET_DB_URL));
        configuration.setUsername(secrets.get(SECRET_DB_USERNAME));
        configuration.setPassword(secrets.get(SECRET_DB_PASSWORD));
        configuration.setDriverClassName(secrets.get(SECRET_DB_DRIVER));
        configuration.setMaximumPoolSize(10);
        configuration.setMinimumIdle(1);
        configuration.setPoolName("UcoHikariPool");

        return new HikariDataSource(configuration);
    }
}