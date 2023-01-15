package data;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Interface to represent an Agenda Item as featured in the Parliament Protocols.
 * Provides the methods to get the required data.
 * @author DavidJordan
 */
public interface AgendaItem {

    /**
     * Gets the ID of this AgendaItem which is a String concatenation of the following form:
     * electionPeriod/protocolNumber/tagesordnungspunktname, z.B. "19/70/Tagesordnungspunkt 1"
     * @return id the unique ID of this AgendaItem
     * @author DavidJordan
     */
    String getID();

    /**
     * Gets the String representation of the Date of the Session.
     * @return date
     * @author DavidJordan
     */
    LocalDate getDate();

    /**
     * Gets a list of all the IDs of the speeches contained in this Agenda Item.
     * @return speechIDs
     * @author DavidJordan
     */
    ArrayList<String> getSpeechIDs();

    /**
     * Gets the subject of this Agenda Item.
     * @return subject
     * @author DavidJordan
     */
    String getSubject();
}
