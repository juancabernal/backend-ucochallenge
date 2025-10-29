package co.edu.uco.ucochallenge.crosscuting.messages;

import java.util.Collections;
import java.util.Map;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageCatalogHolder;

public final class MessageProvider {

        private MessageProvider() {
        }

        public static String getMessage(final String code) {
                return MessageCatalogHolder.getMessage(code);
        }

        public static String getMessage(final String code, final Map<String, String> parameters) {
                return MessageCatalogHolder.getMessage(code, ObjectHelper.getDefault(parameters, Collections.emptyMap()));
        }

        public static String getMessage(final String code, final String defaultMessage) {
                final String message = getMessage(code);
                return TextHelper.isEmpty(message) ? defaultMessage : message;
        }

        public static String getMessage(final String code, final Map<String, String> parameters,
                        final String defaultMessage) {
                final String message = getMessage(code, parameters);
                return TextHelper.isEmpty(message) ? defaultMessage : message;
        }
}
