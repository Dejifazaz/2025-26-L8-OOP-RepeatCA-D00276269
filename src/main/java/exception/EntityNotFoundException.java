package exception;

/**
 * Thrown when a requested entity cannot be found in the database.
 * @author D00276269
 */
public class EntityNotFoundException extends RuntimeException {

    private final int entityId;
    private final String entityType;

    public EntityNotFoundException(String entityType, int entityId) {
        super(entityType + " not found for id " + entityId);
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }
}