package data;

import java.util.ArrayList;

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
   String getDate();
}
