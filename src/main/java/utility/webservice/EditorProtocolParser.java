package utility.webservice;

import com.mongodb.client.model.UpdateOptions;
import data.Comment;
import data.impl.AgendaItem_Impl;
import data.impl.Comment_Impl;
import data.impl.Protocol_Impl;
import data.impl.Speech_Impl;
import exceptions.EditorFormattingException;
import exceptions.WrongInputException;
import org.bson.Document;
import utility.MongoDBHandler;
import utility.UIMAPerformer;
import utility.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static utility.TimeHelper.*;

/**
 * The {@code EditorProtocolParser} class is used to parse the protocols, agenda items and
 * speeches edited and written in the web application.
 * @author Eric Lakhter
 */
public class EditorProtocolParser {
    private final MongoDBHandler mdbh;
    private final UpdateOptions uo = new UpdateOptions().upsert(true);
    private final UIMAPerformer uima;
    private final String[] protocolReqs = {"[PROTOKOLL]", "[DATUM]", "[BEGINN]", "[ENDE]", "[SITZUNGSLEITER]", "[TOPS]"};
    private final String[] agendaReqs = {"[PROTOKOLL]", "[TOP]"};
    private final String[] speechReqs = {"[PROTOKOLL]", "[REDEID]", "[REDNERID]"};


    /**
     * Initiates a new {@code EditorProtocolParser}.
     * @param mdbh MongoDBConnection to check/insert documents in.
     */
    public EditorProtocolParser(MongoDBHandler mdbh, UIMAPerformer uima) {
        this.mdbh = mdbh;
        this.uima = uima;
    }

    /**
     * Parses a String and tries to convert it into a viable {@link Protocol_Impl}.
     * @param rawText The String to be parsed.
     * @param allowOverwrite if true, allows the protocol with the
     *        matching ID to be overwritten in the database.
     * @throws EditorFormattingException if any part of the text does
     *         not fit the standards for editing/creating protocols.
     */
    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorProtocol(String rawText, boolean allowOverwrite) throws EditorFormattingException, WrongInputException {
        if (rawText == null || rawText.isEmpty())
            throw new EditorFormattingException("Text area is empty");
        String[] lines = rawText.split("\n");
        String protocolID = null;
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
                protocolID = electionPeriod + "/" + protocolNumber;
                // TODO remove testing
//                if (!allowOverwrite && mdbh.checkIfDocumentExists("protocol", id))
                if (!allowOverwrite && testVersionExists("protocol", protocolID))
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
        insertIntoTest(new Protocol_Impl(protocolID, date, begin, end, durationBetweenTimesInMinutes(begin, end),
                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));
        // TODO remove testing
//        mdbh.insertProtocol(new Protocol_Impl(id, date, begin, end, durationBetweenTimesInMinutes(begin, end),
//                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));
        return protocolID;
    }

    public String parseEditorAgendaItem(String rawText, boolean allowOverwrite) throws EditorFormattingException {
        if (rawText == null || rawText.isEmpty())
            throw new EditorFormattingException("Text area is empty");
        String[] lines = rawText.split("\n");
        String protocolID = null;
        String agendaID = null;
        String subject = "";
        ArrayList<String> speechIDs = new ArrayList<>(0);

        Set<String> check = new HashSet<>(2);

        for (String line : lines) {
            if (line.startsWith("[PROTOKOLL]")) {
                int[] periodAndProtocol = validateProtocolID(
                        line.substring(11).trim());
                protocolID = periodAndProtocol[0] + "/" + periodAndProtocol[1];
                check.add("[PROTOKOLL]");
            }
            else if (line.startsWith("[TOP]")) {
                agendaID = line.substring(5).trim();
                check.add("[TOP]");
            }
            else if (line.startsWith("[INHALT]")) {
                subject += "\n" +line.substring(8).trim();
            }
            else if (line.startsWith("[REDEIDS]")) {
                String[] sIDs = line.substring(8).trim().split(",\\s*");
                speechIDs.addAll(Arrays.asList(sIDs));
            }
            else
                throw new EditorFormattingException("All lines must begin with a proper code when creating agenda items");
        }
        for (String agendaReq : agendaReqs) {
            if (!check.contains(agendaReq))
                throw new EditorFormattingException("The submitted text is missing a " + agendaReq + " line");
        }
        String fullAgendaID = protocolID + "/" + agendaID;
        // TODO remove testing
//        if (!allowOverwrite && mdbh.checkIfDocumentExists("agendaItem", fullAgendaID))
        if (!allowOverwrite && testVersionExists("agendaItem", fullAgendaID))
            throw new EditorFormattingException("This agenda item exists already and isn't allowed to be overwritten");
        subject =  subject.isEmpty()
                ? agendaID
                : subject.substring(1); ;

        LocalDate date = dateToLocalDate(mdbh.getDocument("protocol", protocolID).getDate("date"));
        insertIntoTest(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs));
        // TODO remove testing
//        mdbh.insertAgendaItems(Collections.singletonList(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs)));
        return fullAgendaID;
    }
    public String parseEditorSpeech(String rawText, boolean allowOverwrite) throws EditorFormattingException {
        if (rawText == null || rawText.isEmpty())
            throw new EditorFormattingException("Text area is empty");
        String[] lines = rawText.split("\n");
        String protocolID = null;
        String speechID = null;
        String speakerID = null;
        String text = "";
        List<Comment> comments = new ArrayList<>();

        Set<String> check = new HashSet<>(2);

        for (String line : lines) {
            if (line.startsWith("[PROTOKOLL]")) {
                int[] periodAndProtocol = validateProtocolID(
                        line.substring(11).trim());
                protocolID = periodAndProtocol[0] + "/" + periodAndProtocol[1];
                check.add("[PROTOKOLL]");
            }
            else if (line.startsWith("[REDEID]")) {
                speechID = line.substring(8).trim();
            }
            else if (line.startsWith("[REDNERID]")) {
                speakerID = line.substring(10).trim();
            }
            else if (line.startsWith("[KOMMENTAR]")) {
//                comments.add(new Comment_Impl());
            }
            else {
                text += line;
            }
        }
        for (String speechReq : speechReqs) {
            if (!check.contains(speechReq))
                throw new EditorFormattingException("The submitted text is missing a " + speechReq + " line");
        }
        // TODO remove testing
//        if (!allowOverwrite && mdbh.checkIfDocumentExists("agendaItem", fullAgendaID))
        if (!allowOverwrite && testVersionExists("speech", speechID))
            throw new EditorFormattingException("This agenda item exists already and isn't allowed to be overwritten");

        LocalDate date = dateToLocalDate(mdbh.getDocument("protocol", protocolID).getDate("date"));
        // TODO remove testing
//        mdbh.insertAgendaItems(Collections.singletonList(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs)));
//        insertIntoTest(uima.processSpeech(new Speech_Impl()));
        return speechID;
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

    // FOR TESTING

    @Testing
    private void insertIntoTest(Object insertObject) {
//        if (insertObject instanceof Protocol_Impl) {
//            Protocol_Impl protocol = (Protocol_Impl) insertObject;
//            mdbh.getDB().getCollection("editor_test_protocol")
//                    .updateOne(new Document("_id", protocol.getID()), new Document(
//
//                    ), uo);
//        } else if (insertObject instanceof AgendaItem_Impl) {
//            AgendaItem_Impl aItem = (AgendaItem_Impl) insertObject;
//            mdbh.getDB().getCollection("editor_test_agendaItem")
//                    .updateOne(new Document("_id", aItem.getID()), new Document(
//
//                    ), uo);
//        } else if (insertObject instanceof ProcessedSpeech) {
//            ProcessedSpeech speech = (ProcessedSpeech) insertObject;
//            mdbh.getDB().getCollection("editor_test_speech")
//                    .updateOne(new Document("_id", speech.getID()), new Document(
//
//                    ), uo);
//        }
    }
    @Testing
    private boolean testVersionExists(String col, String id) {
        return mdbh.getDB()
                .getCollection("editor_test_" + col)
                .find(new Document("_id", id))
                .iterator()
                .hasNext();
    }
}
