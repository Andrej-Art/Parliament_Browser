package data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Interface that represents a Comment from a Parliament Protocol. Provides
 * the methods to access the relevant data.
 * @author DavidJordan
 */
public interface Comment {

    /**
     * Gets the ID of the Comment.
     * @return id
     * @author DavidJordan
     */
    String getID();

    /**
     * Gets the ID of the Speech in which this Comment was made.
     * @return speechID
     * @author DavidJordan
     */
    String getSpeechID();

    /**
     * Gets the ID of the Speaker who received this comment.
     * @return speakerID
     * @author DavidJordan
     */
    String getSpeakerID();

    /**
     * Gets the name of the person making this comment if it was made by an individual.
     * @return commentator
     * @author DavidJordan
     */
    String getCommentator();

    /**
     * Gets a list of names of the Bundestags-Fractions who made the Comment.
     * @return fractions
     * @author DavidJordan
     */
    ArrayList<String> getFractions();

    /**
     * Gets the text content of the Comment
     * @return text
     * @author DavidJordan
     */
    String getText();

    /**
     * Gets the ISO date representation of the protocol in which the comment was made.
     * @return date
     * @author DavidJordan
     */
    LocalDate getDate();

}
