package co.edu.uco.ucochallenge.crosscuting.parameters;

import java.util.regex.Pattern;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.ParameterCatalogHolder;

public final class ParameterProvider {

        private ParameterProvider() {
        }

        public static String getString(final String code) {
                return ParameterCatalogHolder.getValue(code);
        }

        public static int getInteger(final String code) {
                return Integer.parseInt(getString(code));
        }

        public static Pattern getPattern(final String code) {
                return Pattern.compile(getString(code));
        }

        public static String getString(final String code, final String defaultValue) {
                final String value = getString(code);
                return TextHelper.isEmpty(value) ? defaultValue : value;
        }
}
