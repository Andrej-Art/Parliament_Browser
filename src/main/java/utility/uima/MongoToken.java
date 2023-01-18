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

    public MongoToken(int startPos, int endPos, String lemmaValue, String pos) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.lemmaValue = lemmaValue;
        this.pos = pos;
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
}
