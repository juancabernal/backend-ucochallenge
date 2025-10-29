package co.edu.uco.ucochallenge.crosscuting.exception;

import java.util.Collections;
import java.util.Map;

import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageCatalogHolder;

public final class InfrastructureException extends UcoChallengeException {

        private static final long serialVersionUID = 1L;

        private InfrastructureException(final String technicalMessage, final String userMessage, final Throwable cause) {
                super(technicalMessage, userMessage, cause);
        }

        public static InfrastructureException build(final String technicalMessage, final String userMessage,
                        final Throwable cause) {
                return new InfrastructureException(technicalMessage, userMessage, cause);
        }

        public static InfrastructureException build(final String technicalMessage, final String userMessage) {
                return new InfrastructureException(technicalMessage, userMessage, null);
        }

        public static InfrastructureException build(final String message) {
                return new InfrastructureException(message, message, null);
        }

        public static InfrastructureException buildFromCatalog(final String technicalCode, final String userCode,
                        final Map<String, String> parameters, final Throwable cause) {
                final String technicalMessage = MessageCatalogHolder.getMessage(technicalCode, parameters);
                final String userMessage = MessageCatalogHolder.getMessage(userCode, parameters);
                return new InfrastructureException(technicalMessage, userMessage, cause);
        }

        public static InfrastructureException buildFromCatalog(final String technicalCode, final String userCode,
                        final Map<String, String> parameters) {
                return buildFromCatalog(technicalCode, userCode, parameters, null);
        }

        public static InfrastructureException buildFromCatalog(final String technicalCode, final String userCode) {
                return buildFromCatalog(technicalCode, userCode, Collections.emptyMap(), null);
        }

        public static InfrastructureException buildFromCatalog(final String messageCode) {
                final String message = MessageCatalogHolder.getMessage(messageCode);
                return new InfrastructureException(message, message, null);
        }
}
