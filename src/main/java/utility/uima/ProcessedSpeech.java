package utility.uima;

import data.Speech;

import java.time.LocalDate;
import java.util.List;

/**
 * The {@code ProcessedSpeech} class. Its primary use is to be parsed into a {@code org.bson.Document}
 * via the {@link ProcessedSpeech#toSpeechJson()} method. The full CAS String and the tokens
 * which aren't part of the JSON String can be requested with their respective getters.
 * @author Eric Lakhter
 */
public class ProcessedSpeech {
    private final String _id, speakerID, text;
    private final LocalDate date;
    private final String fullCas;
    private final double sentiment;
    private final String mainTopic;
    private final String maybeTopic;
    private final List<MongoToken> tokens;
    private final List<MongoSentence> sentences;
    private final List<MongoNamedEntity> namedEntities;

    /**
     * Constructs a ProcessSpeech object which contains all important speech data.
     * @param speech The Speech object
     * @param fullCas The full CAS String.
     * @param sentiment The average sentiment value.
     * @param mainTopic The main topic.
     * @param tokens List of all tokens.
     * @param sentences List of all Sentences.
     * @param namedEntities List of all named entities.
     */
    public ProcessedSpeech(
            Speech speech,
            String fullCas,
            double sentiment,
            String mainTopic,
            String maybeTopic,
            List<MongoToken> tokens,
            List<MongoSentence> sentences,
            List<MongoNamedEntity> namedEntities) {
        this._id = speech.getID();
        this.speakerID = speech.getSpeakerID();
        this.text = speech.getText();
        this.date = speech.getDate();
        this.fullCas = fullCas;
        this.sentiment = sentiment;
        this.mainTopic = mainTopic;
        this.maybeTopic = maybeTopic;
        this.tokens = tokens;
        this.sentences = sentences;
        this.namedEntities = namedEntities;
    }

    /**
     * Gets the speech ID.
     * @return Speech ID.
     * @author Eric Lakhter
     */
    public String getID() {
        return this._id;
    }

    /**
     * Returns the date of this speech.
     * @return LocalDate object of this speech.
     * @author Eric Lakhter
     */
    public LocalDate getDate(){
        return this.date;
    }

    /**
     * Gets the processed speech's full CAS string.
     * @return Full CAS string.
     * @author Eric Lakhter
     */
    public String getFullCas() {
        return this.fullCas;
    }

    /**
     * Converts almost all fields (excluding {@code fullCas} and {@code tokens})
     * into a String compatible with {@code org.bson.Document.parse()}.<br>
     * Intended to go into the {@code speech} collection.
     * @return JSON String.
     * @author Eric Lakhter
     */
    public String toSpeechJson() {
        StringBuilder jsonString = new StringBuilder(
                "{\n  _id:\"" + _id + "\","
                + "\n  speakerID:\"" + speakerID + "\","
                + "\n  text:\"" + text.replace("\"", "\\\"") + "\",");

        jsonString.append("\n  sentiment:").append(sentiment)
                .append(",\n  mainTopic:\"").append(mainTopic)
                .append("\",\n  maybeTopic:\"").append(maybeTopic).append("\",");

        // build sentences field
        StringBuilder sentenceString = new StringBuilder("\n  sentences:[");
        for (MongoSentence sentence : sentences) {
            sentenceString.append("\n    {startPos:")
                    .append(sentence.getStartPos())
                    .append(",endPos:")
                    .append(sentence.getEndPos())
                    .append(",sentiment:")
                    .append(sentence.getSentiment())
                    .append("},");
        }
        sentenceString.append("\n  ],");
        jsonString.append(sentenceString);

        // build named_entity fields
        StringBuilder perString = new StringBuilder("\n  namedEntitiesPer:[");
        StringBuilder orgString = new StringBuilder("\n  namedEntitiesOrg:[");
        StringBuilder locString = new StringBuilder("\n  namedEntitiesLoc:[");
        for (MongoNamedEntity ne : namedEntities) {
            switch (ne.getEntityType()) {
                case "PER":
                    perString.append("\n    {startPos:")
                            .append(ne.getStartPos())
                            .append(",endPos:")
                            .append(ne.getEndPos())
                            .append(",coveredText:\"")
                            .append(ne.getCoveredText())
                            .append("\"},");
                    break;
                case "ORG":
                    orgString.append("\n    {startPos:")
                            .append(ne.getStartPos())
                            .append(",endPos:")
                            .append(ne.getEndPos())
                            .append(",coveredText:\"")
                            .append(ne.getCoveredText())
                            .append("\"},");
                    break;
                case "LOC":
                    locString.append("\n    {startPos:")
                            .append(ne.getStartPos())
                            .append(",endPos:")
                            .append(ne.getEndPos())
                            .append(",coveredText:\"")
                            .append(ne.getCoveredText())
                            .append("\"},");
                break;
            }
        }
        perString.append("\n  ],");
        orgString.append("\n  ],");
        locString.append("\n  ]");
        jsonString.append(perString).append(orgString).append(locString)
                .append("\n}");
        return jsonString.toString();
    }

    /**
     * Converts almost all fields (excluding {@code fullCas} and {@code tokens})
     * into a String compatible with {@code org.bson.Document.parse()}.<br>
     * Intended to go into the {@code speech_tokens} collection.
     * @return JSON String.
     * @author Eric Lakhter
     */
    public String toSpeechTokensJson() {
        StringBuilder jsonString = new StringBuilder("{\n  _id:\"" + _id
                + "\",\n  speakerID:\"" + speakerID + "\"");

        // build tokens field
        StringBuilder tokenString = new StringBuilder(",\n  tokens:[");
        for (MongoToken token : tokens) {
            tokenString.append("\n    {startPos:")
                    .append(token.getStartPos())
                    .append(",endPos:")
                    .append(token.getEndPos())
                    .append(",lemmaValue:\"")
                    .append(token.getLemmaValue().replace("\"", "\\\""))
                    .append("\",POS:\"")
                    .append(token.getPOS())
                    .append("\",coarsePOS:\"")
                    .append(token.getCoarsePOS())
                    .append("\",morphValue:\"")
                    .append(token.getMorphValue())
                    .append("\"},");
        }
        tokenString.append("\n  ],");
        jsonString.append(tokenString).append("\n}");
        return jsonString.toString();
    }
}
