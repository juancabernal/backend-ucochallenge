package co.edu.uco.ucochallenge.crosscuting.integration.http;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Custom {@link SimpleClientHttpRequestFactory} that disables client side caching at the
 * HTTP connection level. This guarantees that every invocation performed through the
 * configured {@link org.springframework.web.client.RestTemplate RestTemplate} results in a
 * fresh request against the remote service.
 */
public class NoCacheSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    @Override
    protected void prepareConnection(final HttpURLConnection connection, final String httpMethod)
            throws IOException {
        super.prepareConnection(connection, httpMethod);
        connection.setUseCaches(false);
    }
}
