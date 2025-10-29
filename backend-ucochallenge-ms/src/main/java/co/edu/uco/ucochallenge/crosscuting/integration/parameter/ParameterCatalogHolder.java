package co.edu.uco.ucochallenge.crosscuting.integration.parameter;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class ParameterCatalogHolder {

        private static volatile ParameterCatalog delegate = new NoOpParameterCatalog();

        private ParameterCatalogHolder() {
                // utility class
        }

        public static void configure(final ParameterCatalog catalog) {
                delegate = ObjectHelper.getDefault(catalog, new NoOpParameterCatalog());
        }

        public static String getValue(final String code) {
                if (TextHelper.isEmpty(code)) {
                        return TextHelper.EMPTY;
                }

                return TextHelper.getDefault(delegate.getValue(code), code);
        }

        private static final class NoOpParameterCatalog implements ParameterCatalog {

                @Override
                public String getValue(final String code) {
                        return TextHelper.getDefault(code);
                }
        }
}
