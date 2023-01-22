package data.impl;

import data.Protocol;
import org.bson.Document;
import utility.TimeHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Class that implements the Protocol Interface. Provides Constructors for parameter-based instantiation of the variables.
 * And for instatiation from a BSON Document. Test to check if previous commit was successful.
 * @author DavidJordan
 */
public class Protocol_Impl implements Protocol {

    private Document protocolDoc;

    private String _id;
    private LocalTime beginTime, endTime;
    private LocalDate date;
    private long duration;

    private int electionPeriod, protocolNumber;

    private ArrayList<String> sessionLeaders, agendaItems;

    /**
     * Full {@code Protocol} constructor that takes the parameters listed below.
     * @param id The Protocol's id
     * @param date The date of the protocolled session
     * @param beginTime The Time when the session starts
     * @param endTime The Time when the session ends
     * @param electionPeriod The election Period the Protocol is from
     * @param protocolNumber The Number of the Protocol
     * @param sessionLeaders The List of all Session leaders for the Protocol
     * @param agendaItems The List of IDs of the AgendaItems featured in this Protocol
     * @author DavidJordan
     */
    public Protocol_Impl(String id, LocalDate date, LocalTime beginTime, LocalTime endTime, int electionPeriod, int protocolNumber, ArrayList<String> sessionLeaders, ArrayList<String> agendaItems){
        this._id = id;
        this.date = date;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.duration = 0;
        this.electionPeriod = electionPeriod;
        this.protocolNumber = protocolNumber;
        this.sessionLeaders = sessionLeaders;
        this.agendaItems = agendaItems;
    }

    /**
     * Full {@code Protocol} constructor that takes the parameters listed below.
     * @param id The Protocol's id
     * @param date The date of the protocolled session
     * @param beginTime The Time when the session starts
     * @param endTime The Time when the session ends
     * @param duration the duration of the session in minutes
     * @param electionPeriod The election Period the Protocol is from
     * @param protocolNumber The Number of the Protocol
     * @param sessionLeaders The List of all Session leaders for the Protocol
     * @param agendaItems The List of IDs of the AgendaItems featured in this Protocol
     * @author DavidJordan
     */
    public Protocol_Impl(String id, LocalDate date, LocalTime beginTime, LocalTime endTime, long duration, int electionPeriod, int protocolNumber, ArrayList<String> sessionLeaders, ArrayList<String> agendaItems){
        this._id = id;
        this.date = date;
        this.beginTime =beginTime;
        this.endTime = endTime;
        this.duration = duration;
        this.electionPeriod = electionPeriod;
        this.protocolNumber = protocolNumber;
        this.sessionLeaders = sessionLeaders;
        this.agendaItems = agendaItems;
    }

    /**
     * Full {@code Protocol} constructor which takes the BSON Document as a parameter and extracts the data from it.
     * @param document Retrieved from the DB
     * @author DavidJordan
     */
    public Protocol_Impl(Document document){
        this.protocolDoc = document;
        this._id = document.getString("_id");
        this.date = TimeHelper.dateToLocalDate(document.getDate("date"));
        this.beginTime = TimeHelper.dateToLocalTime(document.getDate("beginTime"));
        this.endTime = TimeHelper.dateToLocalTime(document.getDate("endTime"));
        this.electionPeriod = document.getInteger("electionPeriod");
        this.protocolNumber = document.getInteger("protocolNumber");
        this.sessionLeaders = (ArrayList<String>) document.get("sessionLeaders");
        this.agendaItems = (ArrayList<String>) document.get("agendaItems");
    }


    @Override
    public String getID() {
        return this._id;
    }

    @Override
    public int getElectionPeriod() {
        return this.electionPeriod;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public int getProtocolNumber() {
        return this.protocolNumber;
    }

    @Override
    public LocalTime getBeginTime() {
        return this.beginTime;
    }

    @Override
    public LocalTime getEndTime() {
        return this.endTime;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public ArrayList<String> getSessionLeaders() {
        return this.sessionLeaders;
    }

    @Override
    public ArrayList<String> getAgendaItemIDs() {
        return this.agendaItems;
    }
}
