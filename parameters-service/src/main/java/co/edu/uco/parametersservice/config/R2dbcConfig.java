package co.edu.uco.parametersservice.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

@Configuration
@EnableConfigurationProperties(R2dbcProperties.class)
@EnableR2dbcRepositories(basePackages = "co.edu.uco.parametersservice.infrastructure.repository")
public class R2dbcConfig {

    @Bean
    public ConnectionFactory connectionFactory(R2dbcProperties properties) {
        ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.parse(properties.getUrl()).mutate();
        if (properties.getUsername() != null) {
            builder.option(ConnectionFactoryOptions.USER, properties.getUsername());
        }
        if (properties.getPassword() != null) {
            builder.option(ConnectionFactoryOptions.PASSWORD, properties.getPassword());
        }
        return ConnectionFactories.get(builder.build());
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
