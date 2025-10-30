package co.edu.uco.messageservice.catalog;

/**
 * Represents a change in the in-memory catalog. The event is propagated
 * through a reactive stream so any subscriber can react immediately.
 */
public record CatalogEvent<T>(CatalogEventType type, T payload) {

    public enum CatalogEventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
