package co.edu.uco.ucochallenge.crosscuting.parameters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.ParameterCatalogHolder;

public final class ParameterProvider {

        private static final Map<String, Integer> INTEGER_CACHE = new ConcurrentHashMap<>();
        private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

        private ParameterProvider() {
        }

        public static String getString(final String code) {
                return ParameterCatalogHolder.getValue(code);
        }

        public static int getInteger(final String code) {
                return INTEGER_CACHE.computeIfAbsent(code, key -> Integer.parseInt(getString(code)));
        }

        public static Pattern getPattern(final String code) {
                return PATTERN_CACHE.computeIfAbsent(code, key -> Pattern.compile(getString(code)));
        }

        public static String getString(final String code, final String defaultValue) {
                final String value = getString(code);
                return TextHelper.isEmpty(value) ? defaultValue : value;
        }
}
