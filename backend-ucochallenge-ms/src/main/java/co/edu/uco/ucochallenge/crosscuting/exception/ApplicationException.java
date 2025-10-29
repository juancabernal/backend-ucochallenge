package co.edu.uco.ucochallenge.crosscuting.exception;

import java.util.Collections;
import java.util.Map;

import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageCatalogHolder;

public final class ApplicationException extends UcoChallengeException {

        private static final long serialVersionUID = 1L;

        private ApplicationException(final String technicalMessage, final String userMessage, final Throwable cause) {
                super(technicalMessage, userMessage, cause);
        }

        public static ApplicationException build(final String technicalMessage, final String userMessage,
                        final Throwable cause) {
                return new ApplicationException(technicalMessage, userMessage, cause);
        }

        public static ApplicationException build(final String technicalMessage, final String userMessage) {
                return new ApplicationException(technicalMessage, userMessage, null);
        }

        public static ApplicationException build(final String message) {
                return new ApplicationException(message, message, null);
        }

        public static ApplicationException buildFromCatalog(final String technicalCode, final String userCode,
                        final Map<String, String> parameters, final Throwable cause) {
                final String technicalMessage = MessageCatalogHolder.getMessage(technicalCode, parameters);
                final String userMessage = MessageCatalogHolder.getMessage(userCode, parameters);
                return new ApplicationException(technicalMessage, userMessage, cause);
        }

        public static ApplicationException buildFromCatalog(final String technicalCode, final String userCode,
                        final Map<String, String> parameters) {
                return buildFromCatalog(technicalCode, userCode, parameters, null);
        }

        public static ApplicationException buildFromCatalog(final String technicalCode, final String userCode) {
                return buildFromCatalog(technicalCode, userCode, Collections.emptyMap(), null);
        }

        public static ApplicationException buildFromCatalog(final String messageCode) {
                final String message = MessageCatalogHolder.getMessage(messageCode);
                return new ApplicationException(message, message, null);
        }
}
