package utility.uima;

/**
 * The {@code MongoNamedEntity} class.
 *
 * @author Eric Lakhter
 */
public class MongoNamedEntity {
    private final int startPos;
    private final int endPos;
    private final String entityType;

    public MongoNamedEntity(int startPos, int endPos, String entityType) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.entityType = entityType;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public String getEntityType() {
        return entityType;
    }
}
