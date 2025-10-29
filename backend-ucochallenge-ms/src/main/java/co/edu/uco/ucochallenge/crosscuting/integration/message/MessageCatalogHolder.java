package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.edu.uco.ucochallenge.crosscuting.exception.UcoChallengeException;
import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class MessageCatalogHolder {

        private static final Logger LOGGER = LoggerFactory.getLogger(MessageCatalogHolder.class);
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
                try {
                        final String resolvedMessage = TextHelper.getDefault(delegate.getMessage(code, safeParameters), code);
                        CACHE.put(cacheKey, resolvedMessage);
                        return resolvedMessage;
                } catch (final UcoChallengeException exception) {
                        LOGGER.warn("Unable to resolve message '{}' from catalog. Using fallback value.", code, exception);
                        return code;
                } catch (final RuntimeException exception) {
                        LOGGER.error("Unexpected error while resolving message '{}' from catalog. Using fallback value.", code,
                                        exception);
                        return code;
                }
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
