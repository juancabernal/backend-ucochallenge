package co.edu.uco.ucochallenge.crosscuting.messages;

import java.util.Collections;
import java.util.Map;

import co.edu.uco.ucochallenge.crosscuting.integration.message.MessageCatalogHolder;

public final class MessageProvider {

    private MessageProvider() {
    }

    public static String getMessage(final String key) {
        return MessageCatalogHolder.getCatalog().getMessage(key, Collections.emptyMap());
    }

    public static String getMessage(final String key, final Map<String, String> parameters) {
        return MessageCatalogHolder.getCatalog().getMessage(key, parameters);
    }
}
