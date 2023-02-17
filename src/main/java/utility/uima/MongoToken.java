package utility.uima;

/**
 * The {@code MongoToken} class holds information for a single Token.
 *
 * @author Eric Lakhter
 */
public class MongoToken {
    private final int startPos;
    private final int endPos;
    private final String lemmaValue;
    private final String pos;
    private final String coarsePos;
    private final String morphValue;

    /**
     * A MongoToken holds information for a single Token to be inserted into the database.
     * @param startPos Starting position of the token.
     * @param endPos Ending position of the token.
     * @param lemmaValue Lemma value of the token.
     * @param pos Part of speech of the token.
     * @param coarsePos Coarse pos of the token.
     * @param morphValue Holds various information on the token.
     */

    public MongoToken(int startPos, int endPos, String lemmaValue, String pos, String coarsePos, String morphValue) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.lemmaValue = lemmaValue;
        this.pos = pos;
        this.coarsePos = coarsePos;
        this.morphValue = morphValue;
    }

    /**
     * Gets the starting position of the token.
     * @return Starting position of the token.
     * @author Eric Lakhter
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * Gets the ending position of the token.
     * @return Ending position of the token.
     * @author Eric Lakhter
     */
    public int getEndPos() {
        return endPos;
    }

    /**
     * Gets the lemma value of the token.
     * @return Lemma value of the token.
     * @author Eric Lakhter
     */
    public String getLemmaValue() {
        return lemmaValue;
    }

    /**
     * Gets the part of speech of the token.
     * @return Part of speech of the token.
     * @author Eric Lakhter
     */
    public String getPOS() {
        return pos;
    }

    /**
     * Gets the coarse pos of the token.
     * @return Coarse pos of the token.
     * @author Eric Lakhter
     */
    public String getCoarsePOS() {
        return coarsePos;
    }

    /**
     * Gets the various information on the token.
     * @return Holds various information on the token.
     * @author Eric Lakhter
     */
    public String getMorphValue() {
        return morphValue;
    }
}
