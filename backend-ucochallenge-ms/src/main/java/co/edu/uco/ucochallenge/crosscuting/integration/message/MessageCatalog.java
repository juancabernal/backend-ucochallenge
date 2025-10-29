package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Map;

public interface MessageCatalog {

    String getMessage(String key);

    String getMessage(String key, Map<String, String> parameters);
}
