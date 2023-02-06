package utility.webservice;

import data.impl.Protocol_Impl;
import exceptions.EditorFormattingException;
import exceptions.WrongInputException;
import utility.MongoDBHandler;
import utility.annotations.Unfinished;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static utility.TimeHelper.*;

/**
 * The {@code EditorProtocolParser} class is used to parse the protocols, agenda items and
 * speeches edited and written in the web application.
 * @author Eric Lakhter
 */
public class EditorProtocolParser {
    private final MongoDBHandler mdbh;
    private final String[] protocolReqs = {"[PROTOKOLL]", "[DATUM]", "[BEGINN]", "[ENDE]", "[SITZUNGSLEITER]", "[TOPS]"};
    private final String[] agendaReqs = {"[PROTOKOLL]", "[TOP]", "[INHALT]", "[REDEID]"};
    private final String[] speechReqs = {"[PROTOKOLL]", "[TOP]", "[REDEID]", "[REDNERID]"};


    /**
     * Initiates a new {@code EditorProtocolParser}.
     * @param mdbh MongoDBConnection to check/insert documents in.
     */
    public EditorProtocolParser(MongoDBHandler mdbh) {
        this.mdbh = mdbh;
    }

    /**
     * Parses a String and tries to convert it into a viable {@link Protocol_Impl}.
     * @param rawText The String to be parsed.
     * @param allowOverwrite if true, allows the protocol with the
     *        matching ID to be overwritten in the database.
     * @throws EditorFormattingException if any part of the text does
     *         not fit the standards for editing/creating protocols.
     */
    @Unfinished("Only validates protocol number so far, doesn't insert anything yet")
    public void parseEditorProtocol(String rawText, boolean allowOverwrite) throws EditorFormattingException, WrongInputException {
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
        Set<String> check = new HashSet<>(6);

        for (String line : lines) {
            if (line.startsWith("[PROTOKOLL]")) {
                int[] periodAndProtocol = validateProtocolID(
                        line.substring(11).trim());
                electionPeriod = periodAndProtocol[0];
                protocolNumber = periodAndProtocol[1];
                id = electionPeriod + "/" + protocolNumber;
                if (!allowOverwrite && mdbh.checkIfDocumentExists("protocol", id))
                    throw new EditorFormattingException("This protocol exists already and isn't allowed to be overwritten");
                check.add("[PROTOKOLL]");
            }
            else if (line.startsWith("[DATUM]")) {
                date = convertToISOdate(line.substring(7));
                check.add("[DATUM]");
            }
            else if (line.startsWith("[BEGINN]")) {
                begin = convertToISOtime(line.substring(8));
                check.add("[BEGINN]");
            }
            else if (line.startsWith("[ENDE]")) {
                end = convertToISOtime(line.substring(6));
                check.add("[ENDE]");
            }
            else if (line.startsWith("[SITZUNGSLEITER]")) {
                String[] leaders = line.substring(16).trim().split(",\\s*");
                if (leaders.length != 0) {
                    sessionLeaders.addAll(Arrays.asList(leaders));
                } else throw new EditorFormattingException("A protocol needs at least one session leader");
                check.add("[SITZUNGSLEITER]");
            }
            else if (line.startsWith("[TOPS]")) {
                String[] aItems = line.substring(6).trim().split(",\\s*");
                if (aItems.length != 0) {
                    agendaIDs.addAll(Arrays.asList(aItems));
                } else throw new EditorFormattingException("A protocol needs at least one agenda item");
                check.add("[TOPS]");
            }
            else
                throw new EditorFormattingException("All lines must begin with a proper code when creating protocols");
        }

        for (String protocolReq : protocolReqs) {
            if (!check.contains(protocolReq))
                throw new EditorFormattingException("The submitted text is missing a " + protocolReq + " line");
        }

//        mdbh.insertProtocol(new Protocol_Impl(id, date, begin, end, durationBetweenTimesInMinutes(begin, end),
//                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));
    }
    public void parseEditorAgendaItem(String rawText, boolean allowOverwrite) throws EditorFormattingException {
    }
    public void parseEditorSpeech(String rawText, boolean allowOverwrite) throws EditorFormattingException {
    }

    /**
     * Checks whether a given ID can be inserted into the database.
     * @param id Protocol ID, format: {@code x/y}.
     * @return If successful, returns int[2] containing the election period and protocol number.
     * @throws EditorFormattingException if at any point the ID appears to be unviable.
     */
    private int[] validateProtocolID(String id) throws EditorFormattingException {
        try {
            String[] periodAndProtocol = id.split("/");
            if (periodAndProtocol.length != 2)
                throw new EditorFormattingException("Protocol ID has illegal format");
            return new int[] {Integer.parseInt(periodAndProtocol[0]), Integer.parseInt(periodAndProtocol[1])};
        } catch (NumberFormatException e) {
            throw new EditorFormattingException("Protocol ID can only contain integers divided by a slash");
        }
    }
}
