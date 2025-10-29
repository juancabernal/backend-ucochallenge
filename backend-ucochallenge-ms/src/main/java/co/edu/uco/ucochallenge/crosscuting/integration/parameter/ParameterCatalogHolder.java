package co.edu.uco.ucochallenge.crosscuting.integration.parameter;

import java.util.concurrent.atomic.AtomicReference;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public final class ParameterCatalogHolder {

    private static final AtomicReference<ParameterCatalog> HOLDER =
            new AtomicReference<>(key -> TextHelper.getDefault(key));

    private ParameterCatalogHolder() {
    }

    public static void configure(final ParameterCatalog catalog) {
        HOLDER.set(ObjectHelper.getDefault(catalog, key -> TextHelper.getDefault(key)));
    }

    public static ParameterCatalog getCatalog() {
        return HOLDER.get();
    }
}
