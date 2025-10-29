package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class MessageCatalogHolder {

        private static volatile MessageCatalog delegate = new NoOpMessageCatalog();
        private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

        private MessageCatalogHolder() {
                // utility class
        }

        public static void configure(final MessageCatalog catalog) {
                delegate = ObjectHelper.getDefault(catalog, new NoOpMessageCatalog());
                CACHE.clear();
        }

        public static String getMessage(final String code, final Map<String, String> parameters) {
                if (TextHelper.isEmpty(code)) {
                        return TextHelper.EMPTY;
                }
                final Map<String, String> safeParameters = ObjectHelper.getDefault(parameters, Collections.emptyMap());
                final String cacheKey = buildCacheKey(code, safeParameters);
                final String cachedMessage = CACHE.get(cacheKey);
                if (cachedMessage != null) {
                        return cachedMessage;
                }
                final String rawMessage = delegate.getMessage(code, safeParameters);
                final String resolvedMessage = TextHelper.isEmpty(rawMessage) ? code : rawMessage;
                CACHE.put(cacheKey, resolvedMessage);
                return resolvedMessage;
        }

        public static String getMessage(final String code) {
                return getMessage(code, Collections.emptyMap());
        }

        private static String buildCacheKey(final String code, final Map<String, String> parameters) {
                if (parameters.isEmpty()) {
                        return code;
                }
                final StringBuilder builder = new StringBuilder(code);
                parameters.entrySet().stream().sorted(Map.Entry.comparingByKey())
                                .forEach(entry -> builder.append('|').append(entry.getKey()).append('=').append(entry.getValue()));
                return builder.toString();
        }

        private static final class NoOpMessageCatalog implements MessageCatalog {

                @Override
                public String getMessage(final String code, final Map<String, String> parameters) {
                        return TextHelper.getDefault(code);
                }
        }
}
