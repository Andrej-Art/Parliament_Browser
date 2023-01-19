package data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Interface to represent a Speech from a Parliament Protocol. Provides the methods
 * to get the relevant data.
 * @author DavidJordan
 */
public interface Speech {

    /**
     * Gets the ID of the Speech.
     * @return id
     * @author DavidJordan
     */
    String getID();

    /**
     * Gets the ID of the Speaker holding this Speech.
     * @return speakerID
     * @author DavidJordan
     */
    String getSpeakerID();

    /**
     * Gets the text content of the Speech without the comments the speaker received.
     * @return text
     * @author DavidJordan
     */
    String getText();

    /**
     * Gets the String representation of the date on which this speech was held.
     * @return date
     * @author DavidJordan
     */
    LocalDate getDate();

    /**
     * Gets the comment data for this speech.<br>
     * Each entry is a length 2 array which has the comment ID at index 0 and text position at index 1.
     * @return Comment data array.
     * @author Eric Lakhter
     */
    List<String[]> getCommentData();
}
