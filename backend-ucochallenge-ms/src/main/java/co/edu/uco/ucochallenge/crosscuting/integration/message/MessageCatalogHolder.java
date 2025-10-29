package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.edu.uco.ucochallenge.crosscuting.exception.UcoChallengeException;
import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class MessageCatalogHolder {

        private static final Logger LOGGER = LoggerFactory.getLogger(MessageCatalogHolder.class);
        private static volatile MessageCatalog delegate = new NoOpMessageCatalog();
        private static final ThreadLocal<Boolean> REENTRANT_GUARD = ThreadLocal.withInitial(() -> Boolean.FALSE);

        private MessageCatalogHolder() {
                // utility class
        }

        public static void configure(final MessageCatalog catalog) {
                delegate = ObjectHelper.getDefault(catalog, new NoOpMessageCatalog());
        }

        public static String getMessage(final String code, final Map<String, String> parameters) {
                if (TextHelper.isEmpty(code)) {
                        return TextHelper.EMPTY;
                }
                final Map<String, String> safeParameters = ObjectHelper.getDefault(parameters, Collections.emptyMap());
                if (Boolean.TRUE.equals(REENTRANT_GUARD.get())) {
                        LOGGER.warn("Detected recursive attempt to resolve message '{}' from catalog. Using fallback value.",
                                        code);
                        return code;
                }

                REENTRANT_GUARD.set(Boolean.TRUE);
                try {
                        final String resolvedMessage = TextHelper.getDefault(delegate.getMessage(code, safeParameters), code);
                        return resolvedMessage;
                } catch (final UcoChallengeException exception) {
                        LOGGER.warn("Unable to resolve message '{}' from catalog. Using fallback value.", code, exception);
                        return code;
                } catch (final RuntimeException exception) {
                        LOGGER.error("Unexpected error while resolving message '{}' from catalog. Using fallback value.", code,
                                        exception);
                        return code;
                } finally {
                        REENTRANT_GUARD.remove();
                }
        }

        public static String getMessage(final String code) {
                return getMessage(code, Collections.emptyMap());
        }

        private static final class NoOpMessageCatalog implements MessageCatalog {

                @Override
                public String getMessage(final String code, final Map<String, String> parameters) {
                        return TextHelper.getDefault(code);
                }
        }
}
