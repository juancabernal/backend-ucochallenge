package co.edu.uco.ucochallenge.crosscuting.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageCatalog;
import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageCatalogHolder;
import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageServiceRestClient;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.ParameterCatalog;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.ParameterCatalogHolder;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.ParameterServiceRestClient;

@Configuration
@EnableConfigurationProperties(ServiceEndpointsProperties.class)
public class ServiceClientsConfiguration {

        @Bean
        public RestTemplate restTemplate() {
                return new RestTemplate();
        }

        @Bean
        public MessageCatalog messageCatalog(final RestTemplate restTemplate,
                        final ServiceEndpointsProperties properties) {
                final MessageCatalog catalog = new MessageServiceRestClient(restTemplate, properties.getMessage());
                MessageCatalogHolder.configure(catalog);
                return catalog;
        }

        @Bean
        public ParameterCatalog parameterCatalog(final RestTemplate restTemplate,
                        final ServiceEndpointsProperties properties) {
                final ParameterCatalog catalog = new ParameterServiceRestClient(restTemplate, properties.getParameter());
                ParameterCatalogHolder.configure(catalog);
                return catalog;
        }
}
