package co.edu.uco.ucochallenge.crosscuting.integration.parameter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class ParameterCatalogHolder {

        private static volatile ParameterCatalog delegate = new NoOpParameterCatalog();
        private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

        private ParameterCatalogHolder() {
                // utility class
        }

        public static void configure(final ParameterCatalog catalog) {
                delegate = ObjectHelper.getDefault(catalog, new NoOpParameterCatalog());
                CACHE.clear();
        }

        public static String getValue(final String code) {
                if (TextHelper.isEmpty(code)) {
                        return TextHelper.EMPTY;
                }
                final String cachedValue = CACHE.get(code);
                if (cachedValue != null) {
                        return cachedValue;
                }
                final String resolvedValue = TextHelper.getDefault(delegate.getValue(code), code);
                CACHE.put(code, resolvedValue);
                return resolvedValue;
        }

        private static final class NoOpParameterCatalog implements ParameterCatalog {

                @Override
                public String getValue(final String code) {
                        return TextHelper.getDefault(code);
                }
        }
}
