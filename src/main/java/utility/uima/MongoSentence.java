package utility.uima;

/**
 * The {@code MOngoSentence} class.
 *
 * @author Eric Lakhter
 */
public class MongoSentence {
    private final int startPos;
    private final int endPos;
    private final double sentiment;

    public MongoSentence(int startPos, int endPos, double sentiment) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.sentiment = sentiment;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public double getSentiment() {
        return sentiment;
    }
}
