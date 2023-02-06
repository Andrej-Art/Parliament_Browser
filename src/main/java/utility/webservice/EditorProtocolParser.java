package utility.webservice;

import data.impl.Protocol_Impl;
import exceptions.EditorFormattingException;
import exceptions.WrongInputException;
import utility.MongoDBHandler;
import utility.annotations.Unfinished;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static utility.TimeHelper.*;

/**
 * The {@code EditorProtocolParser} class is used to parse the protocols, agenda items and speeches edited and written
 * in the web application.
 * @author Eric Lakhter
 */
public class EditorProtocolParser {
    private final MongoDBHandler mdbh;

    /**
     * Initiates a new {@code EditorProtocolParser}.
     * @param mdbh MongoDBConnection to check/insert documents in.
     */
    public EditorProtocolParser(MongoDBHandler mdbh) {
        this.mdbh = mdbh;
    }

    /**
     *
     * @param rawText
     * @throws EditorFormattingException
     * @throws WrongInputException
     */
    @Unfinished("Only validates protocol number so far, doesn't insert anything yet")
    public void parseEditorProtocol(String rawText) throws EditorFormattingException, WrongInputException {
        if (rawText == null || rawText.isEmpty())
            throw new EditorFormattingException("Text area is empty");
        String[] lines = rawText.split("\n");
        String id = null;
        int electionPeriod = 0;
        int protocolNumber = 0;
        LocalDate date = null;
        LocalTime begin = null, end = null;
        Set<String> sessionLeaders = new HashSet<>(0);
        ArrayList<String> agendaIDs = new ArrayList<>(0);

        for (String line : lines) {
            if (line.startsWith("[PROTOKOLL]")) {
                int[] periodAndProtocol = validateProtocolID(line.substring(11).replaceAll(" ", ""));
                electionPeriod = periodAndProtocol[0];
                protocolNumber = periodAndProtocol[1];
                id = electionPeriod + "/" + protocolNumber;
            } else if (line.startsWith("[DATUM]")) {
                line = line.substring(7).replaceAll(" ", "");
                date = convertToISOdate(line);
            } else if (line.startsWith("[BEGINN]")) {
                line = line.substring(8).replaceAll(" ", "");
                begin = convertToISOtime(line);
            } else if (line.substring(6).startsWith("[ENDE]")) {
                line = line.replaceAll(" ", "");
                end = convertToISOtime(line);
            } else if (line.startsWith("[SITZUNGSLEITER]")) {
                line = line.substring(16);
                sessionLeaders.add(line);
            } else if (line.startsWith("[TOPS]")) {
                line = line.substring(6).replaceAll(" ", "");
                agendaIDs.add(line);
            }
        }
        mdbh.insertProtocol(new Protocol_Impl(id, date, begin, end, durationBetweenTimesInMinutes(begin, end), electionPeriod, protocolNumber, sessionLeaders, agendaIDs));
    }
    public void parseEditorAgendaItem(String rawText) throws EditorFormattingException {
    }
    public void parseEditorSpeech(String rawText) throws EditorFormattingException {
    }

    /**
     * Checks whether a given ID can be inserted into the database.
     * @param id Protocol ID, format: {@code x/y}.
     * @return If successful, returns int[2] containing the election period and protocol number.
     * @throws EditorFormattingException if at any point the ID appears to be unviable.
     */
    private int[] validateProtocolID(String id) throws EditorFormattingException {
        if (mdbh.checkIfDocumentExists("protocol", id))
            throw new EditorFormattingException("A protocol with ID " + id + " already exists");
        try {
            String[] periodAndProtocol = id.split("/");
            if (periodAndProtocol.length != 2)
                throw new EditorFormattingException();
            return new int[] {Integer.parseInt(periodAndProtocol[0]), Integer.parseInt(periodAndProtocol[1])};
        } catch (NumberFormatException | EditorFormattingException e) {
            throw new EditorFormattingException("Protocol ID has illegal format");
        }
    }
}
