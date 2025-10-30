package co.edu.uco.messageservice.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableConfigurationProperties(R2dbcProperties.class)
@EnableR2dbcRepositories(basePackages = "co.edu.uco.messageservice.infrastructure.repository")
public class R2dbcConfig {

    private static final Logger logger = LoggerFactory.getLogger(R2dbcConfig.class);

    @Bean
    public ConnectionFactory connectionFactory(R2dbcProperties properties) {
        String url = properties.getUrl();
        Assert.hasText(url, "Property 'spring.r2dbc.url' must be provided");

        logger.info("Initializing R2DBC connection factory for host defined in URL: {}", url);

        ConnectionFactoryBuilder builder = ConnectionFactoryBuilder.withUrl(url);

        if (properties.getUsername() != null) {
            builder.username(properties.getUsername());
        }
        if (properties.getPassword() != null) {
            builder.password(properties.getPassword());
        }

        return builder.build();
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}
