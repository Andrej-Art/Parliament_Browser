package data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Interface that represents a Parliament Protocol. Provides getter methods
 * to give access to the relevant data.
 * @author DavidJordan
 */
public interface Protocol {


    /**
     * Gets the Protocol's ID consisting of the String concatenation:
     * electionPeriod + "/" + protocollNumber
     * @return id
     * @author DavidJordan
     */
    String getID();

    /**
     * Gets the election Period in which the Protocol is recorded.
     * @return electionPeriod
     * @author DavidJordan
     */
    int getElectionPeriod();

    /**
     * Gets the LocalDate of when the Protocol's Session took place.
     * @return date
     * @author DavidJordan
     */
    LocalDate getDate();

    /**
     * Gets the number of the Protocol in the given election period.
     * @return protocolNumber
     * @author DavidJordan
     */
    int getProtocolNumber();

    /**
     * Gets the LocalTime of the time when the recorded session begins.
     * @return beginTime
     * @author DavidJordan
     */
    LocalTime getBeginTime();

    /**
     * Gets LocalTime of the time when the recorded session ends.
     * @return endTime
     * @author DavidJordan
     */
    LocalTime getEndTime();

    /**
     * Gets a List of all the session leaders in this protocol.
     * @return sessionLeaders
     * @author DavidJordan
     */
    ArrayList<String> getSessionLeaders();

    /**
     * Gets a list of all IDs of the Agenda Items contained in this Protocol.
     * @return agendaItems
     * @author DavidJordan
     */
    ArrayList<String> getAgendaItemIDs();

}
