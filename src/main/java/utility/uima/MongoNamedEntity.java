package utility.uima;

/**
 * The {@code MongoNamedEntity} class holds information for one named entity.
 *
 * @author Eric Lakhter
 */
public class MongoNamedEntity {
    private final int startPos;
    private final int endPos;
    private final String entityType;
    private final String coveredText;

    /**
     * The {@code MongoNamedEntity} class holds information for one named entity to be inserted into the database.
     * @param startPos Starting position of the named entity.
     * @param endPos Ending position of the named entity.
     * @param entityType Type of the named entity.
     * @param coveredText The named entity as written in the text.
     * @author Eric Lakhter
     */

    public MongoNamedEntity(int startPos, int endPos, String entityType, String coveredText) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.entityType = entityType;
        this.coveredText = coveredText;
    }

    /**
     * Gets the starting position of the named entity.
     * @return Starting position of the named entity.
     * @author Eric Lakhter
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * Gets the ending position of the named entity.
     * @return Ending position of the named entity.
     * @author Eric Lakhter
     */
    public int getEndPos() {
        return endPos;
    }

    /**
     * Gets the type of the named entity.
     * @return Type of the named entity.
     * @author Eric Lakhter
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the named entity as written in the text.
     * @return The named entity as written in the text.
     * @author Eric Lakhter
     */
    public String getCoveredText() {
        return coveredText;
    }
}
