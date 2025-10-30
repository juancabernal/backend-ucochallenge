package co.edu.uco.parametersservice.catalog;

/**
 * Generic event describing a change in the parameter catalog.
 */
public record CatalogEvent<T>(CatalogEventType type, T payload) {

    public enum CatalogEventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
