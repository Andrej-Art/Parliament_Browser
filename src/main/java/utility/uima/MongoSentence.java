package utility.uima;

/**
 * The {@code MongoSentence} class holds information for one analyzed sentence.
 *
 * @author Eric Lakhter
 */
public class MongoSentence {
    private final int startPos;
    private final int endPos;
    private final double sentiment;

    /**
     * Holds information for one speech sentence.
     * @param startPos The starting text position of the sentence.
     * @param endPos The ending text position of the sentence.
     * @param sentiment The sentiment of the sentence.
     * @author Eric Lakhter
     */
    public MongoSentence(int startPos, int endPos, double sentiment) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.sentiment = sentiment;
    }

    /**
     * Gets the starting text position of the sentence.
     * @return The starting text position of the sentence.
     * @author Eric Lakhter
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * Gets the ending text position of the sentence.
     * @return The ending text position of the sentence.
     * @author Eric Lakhter
     */
    public int getEndPos() {
        return endPos;
    }

    /**
     * Gets the sentiment value of the sentence.
     * @return The sentiment value of the sentence.
     * @author Eric Lakhter
     */
    public double getSentiment() {
        return sentiment;
    }
}
