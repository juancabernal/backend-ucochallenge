package co.edu.uco.ucochallenge.crosscuting.integration.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import co.edu.uco.ucochallenge.crosscuting.integration.http.NoCacheSimpleClientHttpRequestFactory;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties;

class MessageServiceRestClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private MessageServiceRestClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate(new NoCacheSimpleClientHttpRequestFactory());
        server = MockRestServiceServer.createServer(restTemplate);

        ServiceEndpointsProperties.MessageServiceProperties properties = new ServiceEndpointsProperties.MessageServiceProperties();
        properties.setBaseUrl("http://message-service");

        client = new MessageServiceRestClient(restTemplate, properties);
    }

    @Test
    void shouldFetchFreshMessageOnEveryInvocation() {
        String url = "http://message-service/api/v1/messages/error.code";

        server.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"code\":\"error.code\",\"message\":\"Mensaje original\"}", MediaType.APPLICATION_JSON));

        String firstResponse = client.getMessage("error.code", Map.of());
        assertThat(firstResponse).isEqualTo("Mensaje original");

        server.reset();
        server.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"code\":\"error.code\",\"message\":\"Mensaje actualizado\"}", MediaType.APPLICATION_JSON));

        String secondResponse = client.getMessage("error.code", Map.of());
        assertThat(secondResponse).isEqualTo("Mensaje actualizado");

        server.verify();
    }

    @Test
    void shouldPropagateQueryParametersInRequests() {
        String urlWithParams = "http://message-service/api/v1/messages/error.code?name=UCO";

        server.expect(ExpectedCount.once(), requestTo(urlWithParams))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"code\":\"error.code\",\"message\":\"Hola UCO\"}", MediaType.APPLICATION_JSON));

        String response = client.getMessage("error.code", Map.of("name", "UCO"));
        assertThat(response).isEqualTo("Hola UCO");

        server.verify();
    }
}
