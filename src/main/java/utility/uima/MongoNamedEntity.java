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
    private final String coveredText;

    public MongoNamedEntity(int startPos, int endPos, String entityType, String coveredText) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.entityType = entityType;
        this.coveredText = coveredText;
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

    public String getCoveredText() {
        return coveredText;
    }
}
