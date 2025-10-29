package co.edu.uco.ucochallenge.secondary.adapters.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteCatalogEntry(String key, String value) {
}
