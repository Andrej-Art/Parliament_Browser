package utility.uima;

/**
 * The {@code CASItem} class.
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

    public MongoToken(int startPos, int endPos, String lemmaValue, String pos, String coarsePos, String morphValue) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.lemmaValue = lemmaValue;
        this.pos = pos;
        this.coarsePos = coarsePos;
        this.morphValue = morphValue;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public String getLemmaValue() {
        return lemmaValue;
    }

    public String getPOS() {
        return pos;
    }

    public String getCoarsePOS() {
        return coarsePos;
    }

    public String getMorphValue() {
        return morphValue;
    }
}
