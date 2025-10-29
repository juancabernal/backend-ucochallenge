package co.edu.uco.ucochallenge.crosscuting.integration.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import co.edu.uco.ucochallenge.crosscuting.integration.http.NoCacheSimpleClientHttpRequestFactory;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties;

class ParameterServiceRestClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private ParameterServiceRestClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate(new NoCacheSimpleClientHttpRequestFactory());
        server = MockRestServiceServer.createServer(restTemplate);

        ServiceEndpointsProperties.ParameterServiceProperties properties = new ServiceEndpointsProperties.ParameterServiceProperties();
        properties.setBaseUrl("http://parameter-service");

        client = new ParameterServiceRestClient(restTemplate, properties);
    }

    @Test
    void shouldFetchUpdatedParameterValuesOnEachCall() {
        String url = "http://parameter-service/api/v1/parameters/notification.email";

        server.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"code\":\"notification.email\",\"value\":\"soporte@uco.edu.co\"}", MediaType.APPLICATION_JSON));

        String firstResponse = client.getValue("notification.email");
        assertThat(firstResponse).isEqualTo("soporte@uco.edu.co");

        server.reset();
        server.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"code\":\"notification.email\",\"value\":\"ayuda@uco.edu.co\"}", MediaType.APPLICATION_JSON));

        String secondResponse = client.getValue("notification.email");
        assertThat(secondResponse).isEqualTo("ayuda@uco.edu.co");

        server.verify();
    }
}
