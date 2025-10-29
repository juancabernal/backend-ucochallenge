package co.edu.uco.ucochallenge.crosscuting.integration.http;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Custom {@link SimpleClientHttpRequestFactory} that disables the internal HTTP caching
 * mechanism provided by {@link HttpURLConnection}. This guarantees that every request
 * issued through the {@link org.springframework.web.client.RestTemplate} hits the
 * remote service instead of returning a cached response.
 */
public class NoCacheSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    @Override
    protected void prepareConnection(final HttpURLConnection connection, final String httpMethod) throws IOException {
        super.prepareConnection(connection, httpMethod);
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
    }
}
