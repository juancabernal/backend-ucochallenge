package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class MessageCatalogHolder {

    private static final AtomicReference<MessageCatalog> HOLDER = new AtomicReference<>(new NoOpMessageCatalog());

    private MessageCatalogHolder() {
    }

    public static void configure(final MessageCatalog catalog) {
        HOLDER.set(ObjectHelper.getDefault(catalog, new NoOpMessageCatalog()));
    }

    public static MessageCatalog getCatalog() {
        return HOLDER.get();
    }

    private static final class NoOpMessageCatalog implements MessageCatalog {

        @Override
        public String getMessage(final String key) {
            return TextHelper.getDefault(key);
        }

        @Override
        public String getMessage(final String key, final Map<String, String> parameters) {
            return TextHelper.getDefault(key);
        }
    }
}
