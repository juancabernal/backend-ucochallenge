package co.edu.uco.ucochallenge.crosscuting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import co.edu.uco.ucochallenge.crosscuting.integration.http.NoCacheSimpleClientHttpRequestFactory;

@Configuration
public class ServiceClientsConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(new NoCacheSimpleClientHttpRequestFactory());
        restTemplate.getInterceptors().add((request, body, execution) -> {
            final HttpHeaders headers = request.getHeaders();
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
