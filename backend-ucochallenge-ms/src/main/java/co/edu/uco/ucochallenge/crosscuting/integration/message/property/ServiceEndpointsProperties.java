package co.edu.uco.ucochallenge.crosscuting.integration.message.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

@ConfigurationProperties(prefix = "services")
public class ServiceEndpointsProperties {

    private final ServiceEndpointProperties message = new ServiceEndpointProperties();
    private final ServiceEndpointProperties parameter = new ServiceEndpointProperties();

    public ServiceEndpointProperties getMessage() {
        return message;
    }

    public ServiceEndpointProperties getParameter() {
        return parameter;
    }

    public static class ServiceEndpointProperties {
        private String baseUrl;

        public String getBaseUrl() {
            return TextHelper.getDefault(baseUrl);
        }

        public void setBaseUrl(final String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
}
