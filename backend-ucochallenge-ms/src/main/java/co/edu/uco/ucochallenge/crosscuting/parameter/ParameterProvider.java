package co.edu.uco.ucochallenge.crosscuting.parameter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.ParameterCatalogHolder;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;

public final class ParameterProvider {

    private ParameterProvider() {
    }

    public static String getString(final String key) {
        return ParameterCatalogHolder.getCatalog().getValue(key);
    }

    public static int getInteger(final String key) {
        final String value = getString(key);
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException exception) {
            throw InfrastructureException.buildFromCatalog(
                    MessageCodes.Infrastructure.ParameterService.INVALID_RESPONSE_TECHNICAL,
                    MessageCodes.Infrastructure.ParameterService.INVALID_RESPONSE_USER, exception);
        }
    }

    public static Pattern getPattern(final String key) {
        final String value = getString(key);
        try {
            return Pattern.compile(value);
        } catch (final PatternSyntaxException exception) {
            throw InfrastructureException.buildFromCatalog(
                    MessageCodes.Infrastructure.ParameterService.INVALID_RESPONSE_TECHNICAL,
                    MessageCodes.Infrastructure.ParameterService.INVALID_RESPONSE_USER, exception);
        }
    }
}
