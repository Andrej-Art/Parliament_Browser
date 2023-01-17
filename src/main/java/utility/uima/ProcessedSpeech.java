package utility.uima;

import data.Speech;
import org.apache.uima.jcas.JCas;
import utility.UIMAPerformer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The {@code ProcessedSpeech} class. Its use is to be parsed into a {@code org.bson.Document} via the {@link ProcessedSpeech#toSpeechCollectionJson()} method.
 * @author Eric Lakhter
 * @modified DavidJordan
 */
public class ProcessedSpeech {
    private final String _id, speakerID, text;
    private final LocalDate date;
    private final String fullCas;
    private final double sentiment;
    private final String mainTopic;
    private final List<MongoToken> tokens;
    private final List<MongoSentence> sentences;
    private final List<MongoNamedEntity> namedEntities;

    public ProcessedSpeech(
            Speech speech,
            String fullCas,
            double sentiment,
            String mainTopic,
            List<MongoToken> tokens,
            List<MongoSentence> sentences,
            List<MongoNamedEntity> namedEntities) {
        this._id = speech.getID();
        this.speakerID = speech.getSpeakerID();
        this.text = speech.getText();
        this.date = speech.getDate();
        this.fullCas = fullCas;
        this.tokens = tokens;
        this.sentences = sentences;
        this.namedEntities = namedEntities;
        this.sentiment = sentiment;
        this.mainTopic = mainTopic;
    }

    /**
     * Returns this speeches' full cas string.
     * @return This speeches' full cas string.
     */
    public String getFullCas() {
        return fullCas;
    }

    /**
     * Returns the speech's id  as String
     * @return the _id of the speech
     * @author DavidJordan
     */
    public String getID(){
        return _id;
    }

    /**
     * Returns the speaker's ID
     * @return speakerID
     * @author DavidJordan
     */
    public String getSpeakerID(){
        return speakerID;
    }

    /**
     * Returns the date
     * @return date
     * @author DavidJordan
     */
    public LocalDate getDate(){
        return date;
    }

    /**
     * Returns this speeches' tokens.
     * @return This speeches' tokens.
     */
    public List<MongoToken> getTokens() {
        return tokens;
    }

    /**
     * Converts all fields except for {@code fullCas} and {@code tokens} into a String compatible with {@code org.bson.Document.parse()}.
     * @return Json String.
     */
    public String toSpeechCollectionJson() {
        String jsonString = "{\n  _id:\"" + _id
                + "\",\n  speakerID:\"" + speakerID
                + "\",\n  text:\"" + text
                + "\",\n  date:\"" + date
                + "\",\n  sentiment:" + sentiment +
                ",\n  main_topic:\"" + mainTopic + "\"";

        // build sentences field
        StringBuilder sentenceString = new StringBuilder(",\n  sentences:[");
        for (MongoSentence sentence : sentences) {
            sentenceString.append("{start_pos:")
                    .append(sentence.getStartPos())
                    .append(",end_pos:")
                    .append(sentence.getEndPos())
                    .append(",sentiment:")
                    .append(sentence.getSentiment())
                    .append("},");
        }
        sentenceString.append("]");
        jsonString += sentenceString.toString();

        // build named_entity fields
        StringBuilder perString = new StringBuilder(",\n  named_entities_per:[");
        StringBuilder orgString = new StringBuilder(",\n  named_entities_org:[");
        StringBuilder locString = new StringBuilder(",\n  named_entities_loc:[");
        for (MongoNamedEntity ne : namedEntities) {
            switch (ne.getEntityType()) {
                case "PER":
                    perString.append("{start_pos:")
                            .append(ne.getStartPos())
                            .append(",end_pos:")
                            .append(ne.getEndPos())
                            .append(",covered_text:\"")
                            .append(ne.getCoveredText())
                            .append("\"},");
                    break;
                case "ORG":
                    orgString.append("{start_pos:")
                            .append(ne.getStartPos())
                            .append(",end_pos:")
                            .append(ne.getEndPos())
                            .append(",covered_text:\"")
                            .append(ne.getCoveredText())
                            .append("\"},");
                    break;
                case "LOC": locString.append("{start_pos:")
                            .append(ne.getStartPos())
                            .append(",end_pos:")
                            .append(ne.getEndPos())
                            .append(",covered_text:\"")
                            .append(ne.getCoveredText())
                            .append("\"},");
                break;
            }
        }
        perString.append("]");
        orgString.append("]");
        locString.append("]");
        jsonString += perString + orgString.toString() + locString + "\n}";
        return jsonString;
    }
}
