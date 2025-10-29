package co.edu.uco.ucochallenge.crosscuting.integration.message.property;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public class ServiceEndpointsProperties {

        private final MessageServiceProperties message = new MessageServiceProperties();
        private final ParameterServiceProperties parameter = new ParameterServiceProperties();

        public MessageServiceProperties getMessage() {
                return message;
        }

        public ParameterServiceProperties getParameter() {
                return parameter;
        }

        public static class MessageServiceProperties {

                private String baseUrl;
                private String messagePath = "/api/v1/messages/{code}";

                public String getBaseUrl() {
                        return baseUrl;
                }

                public void setBaseUrl(final String baseUrl) {
                        this.baseUrl = baseUrl;
                }

                public String getMessagePath() {
                        return messagePath;
                }

                public void setMessagePath(final String messagePath) {
                        this.messagePath = messagePath;
                }

                public String resolveMessageUrl(final String code) {
                        return String.format("%s%s", Objects.requireNonNull(baseUrl, "services.message.base-url is required"),
                                        messagePath.replace("{code}", code));
                }
        }

        public static class ParameterServiceProperties {

                private String baseUrl;
                private String parameterPath = "/api/v1/parameters/{code}";

                public String getBaseUrl() {
                        return baseUrl;
                }

                public void setBaseUrl(final String baseUrl) {
                        this.baseUrl = baseUrl;
                }

                public String getParameterPath() {
                        return parameterPath;
                }

                public void setParameterPath(final String parameterPath) {
                        this.parameterPath = parameterPath;
                }

                public String resolveParameterUrl(final String code) {
                        return String.format("%s%s",
                                        Objects.requireNonNull(baseUrl, "services.parameter.base-url is required"),
                                        parameterPath.replace("{code}", code));
                }
        }
}
