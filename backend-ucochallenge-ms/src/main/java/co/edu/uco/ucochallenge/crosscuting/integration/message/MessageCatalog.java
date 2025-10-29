package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Map;

public interface MessageCatalog {

        String getMessage(String code, Map<String, String> parameters);
}
