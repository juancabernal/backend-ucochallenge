package co.edu.uco.ucochallenge.secondary.ports.service;

import java.util.Map;

public interface MessageServicePort {

    String getMessage(String key);

    String getMessage(String key, Map<String, String> parameters);
}
