package utility.webservice;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import data.Comment;
import data.Person;
import data.impl.*;
import exceptions.EditorFormattingException;
import exceptions.WrongInputException;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.json.JSONObject;
import utility.MongoDBHandler;
import utility.UIMAPerformer;
import utility.annotations.*;
import utility.uima.ProcessedSpeech;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;
import static utility.PictureScraper.producePictureUrl;
import static utility.TimeHelper.*;

/**
 * The {@code EditorProtocolParser} class is used to parse the protocols, agenda items and
 * speeches edited and written in the web application.
 * @author Eric Lakhter
 */
public class EditorProtocolParser {
    private final MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();
    private final UpdateOptions uo = new UpdateOptions().upsert(true);
    private final UIMAPerformer uima = new UIMAPerformer();
    private final List<String> protocolReqs = asList("protocolID", "date", "begin", "end", "leaders", "aItems");
    private final List<String> agendaReqs = asList("protocolID", "agendaID", "subject", "speechIDs");
    private final List<String> speechReqs = asList("speechID", "speakerID", "text");

    private final List<String> personReqs = asList("personID", "firstName", "lastName", "role", "title", "place",
            "fraction19", "fraction20", "party", "gender", "birthDate", "deathDate", "birthPlace");

    /**
     * Initiates a new {@code EditorProtocolParser}.
     * @author Eric Lakhter
     */
    public EditorProtocolParser() throws FileNotFoundException, UIMAException {
    }

    /**
     * Parses String data from a JSON and tries to convert it into a viable {@link Protocol_Impl}.
     *
     * @param protocolObject JSON with at least all required protocol data.
     * @return The ID of the freshly inserted protocol.
     * @throws EditorFormattingException if any part of the text does
     *                                   not fit the standards for editing/creating protocols.
     * @author Eric Lakhter
     */
    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorProtocol(JSONObject protocolObject) throws EditorFormattingException, WrongInputException {
        if (!protocolObject.keySet().containsAll(protocolReqs))
            throw new EditorFormattingException("Data doesn't contain all required information");

        String protocolID = protocolObject.getString("protocolID").trim();
        if (protocolID.isEmpty())
            throw new EditorFormattingException("Text area is empty");
//        if (!protocolObject.getBoolean("allowOverwrite") && mongoDBHandler.checkIfDocumentExists("protocol", id)) // TODO remove testing
        if (!protocolObject.getBoolean("allowOverwrite") && testVersionExists("protocol", protocolID))
            throw new EditorFormattingException("This protocol exists already and isn't allowed to be overwritten");
        int[] periodAndProtocol = validateProtocolID(protocolID);
        int electionPeriod = periodAndProtocol[0];
        int protocolNumber = periodAndProtocol[1];

        LocalDate date = convertToISOdate(protocolObject.getString("date").trim(), 3);
        LocalTime begin = convertToISOtime(protocolObject.getString("begin").trim());
        LocalTime end = convertToISOtime(protocolObject.getString("end").trim());

        Set<String> sessionLeaders = new HashSet<>(asList(protocolObject.getString("leaders").trim().split(",\\s*")));
        if (sessionLeaders.isEmpty()) {
            throw new EditorFormattingException("The session needs at least one leader");
        }
        ArrayList<String> agendaIDs = new ArrayList<>(asList(protocolObject.getString("aItems").trim().split(",\\s*")));
        if (agendaIDs.isEmpty()) {
            throw new EditorFormattingException("The session needs at least one agenda item");
        }

        insertIntoTest(new Protocol_Impl(protocolID, date, begin, end, durationBetweenTimesInMinutes(begin, end),
                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));

//        mongoDBHandler.insertProtocol(new Protocol_Impl(id, date, begin, end, durationBetweenTimesInMinutes(begin, end), // TODO remove testing
//                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));
        return protocolID;
    }

    /**
     * Parses a String and tries to convert it into a viable {@link AgendaItem_Impl}.
     * @param rawText The String to be parsed.
     * @param allowOverwrite if true, allows overwriting the agenda item with the same ID in the database.
     * @return The ID of the freshly inserted agenda item.
     * @throws EditorFormattingException if any part of the text does
     *                                   not fit the standards for editing/creating protocols.
     * @author Eric Lakhter
     */
    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorAgendaItem(String rawText, boolean allowOverwrite) throws EditorFormattingException, WrongInputException {
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
            } else if (line.startsWith("[TOP]")) {
                agendaID = line.substring(5).trim();
                check.add("[TOP]");
            } else if (line.startsWith("[INHALT]")) {
                subject += "\n" + line.substring(8).trim();
            } else if (line.startsWith("[REDEIDS]")) {
                String[] sIDs = line.substring(8).trim().split(",\\s*");
                speechIDs.addAll(Arrays.asList(sIDs));
            } else
                throw new EditorFormattingException("All lines must begin with a proper code when creating agenda items");
        }
        for (String agendaReq : agendaReqs) {
            if (!check.contains(agendaReq))
                throw new EditorFormattingException("The submitted text is missing a " + agendaReq + " line");
        }
        String fullAgendaID = protocolID + "/" + agendaID;
        // TODO remove testing
//        if (!allowOverwrite && mongoDBHandler.checkIfDocumentExists("agendaItem", fullAgendaID))
        if (!allowOverwrite && testVersionExists("agendaItem", fullAgendaID))
            throw new EditorFormattingException("This agenda item exists already and isn't allowed to be overwritten");
        subject = subject.isEmpty()
                ? agendaID
                : subject.substring(1); // cut off the leading "\n"

        LocalDate date = dateToLocalDate(mongoDBHandler.getDocument("protocol", protocolID).getDate("date"));
        // TODO remove testing
//        mongoDBHandler.insertAgendaItems(Collections.singletonList(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs)));
        insertIntoTest(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs));
        return fullAgendaID;
    }

    /**
     * Parses a String and tries to convert it into a viable {@link Speech_Impl}.
     * @param rawText The String to be parsed.
     * @param allowOverwrite if true, allows overwriting the speech with the same ID in the database.
     * @return The ID of the freshly inserted speech.
     * @throws EditorFormattingException if any part of the text does
     *                                   not fit the standards for editing/creating protocols.
     * @author Eric Lakhter
     */

    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorSpeech(String rawText, boolean allowOverwrite) throws EditorFormattingException, WrongInputException {
        if (rawText == null || rawText.isEmpty())
            throw new EditorFormattingException("Text area is empty");
        String[] lines = rawText.split("\n");

        if (!lines[0].startsWith("[PROTOKOLL]"))
            throw new EditorFormattingException("First line must start with \"[PROTOKOLL]\"");
        int[] periodAndProtocol = validateProtocolID(lines[0].substring(11).trim());
        String protocolID = periodAndProtocol[0] + "/" + periodAndProtocol[1];
        LocalDate date = dateToLocalDate(mongoDBHandler.getDocument("protocol", protocolID).getDate("date"));

        if (!lines[1].startsWith("[REDEID]"))
            throw new EditorFormattingException("Second line must start with \"[REDEID]\"");
        String speechID = lines[1].substring(8).trim();

        if (!lines[2].startsWith("[REDNERID]"))
            throw new EditorFormattingException("Third line must start with \"[REDNERID]\"");
        String speakerID = lines[2].substring(10).trim();

        String text = "";
        List<Comment> comments = new ArrayList<>();
        ArrayList<Person_Impl> personList = mongoDBHandler.getPersons();

        int commentCounter = 0;

        for (int i = 3; i < lines.length; i++) {

            if (lines[i].startsWith("[KOMMENTAR]")) {
                commentCounter++;
                String commentText = lines[i].substring(11).trim();
                String commentatorID = "";
                for (Person person : personList) {
                    String fullName = person.getFullName();
                    if (commentText.indexOf(fullName) < commentText.indexOf("[") && commentText.contains(fullName)) {
                        commentatorID = person.getID();
                        break;
                    }
                }
                comments.add(new Comment_Impl(speechID + "/" + commentCounter, speechID, speakerID, commentatorID, text.length(), text, date, new ArrayList<>()));
            } else {
                text += " " + lines[i].trim();
            }
        }

        if (text.isEmpty())
            throw new EditorFormattingException("The speech's text cannot be empty");
        // TODO remove testing
//        if (!allowOverwrite && mongoDBHandler.checkIfDocumentExists("agendaItem", fullAgendaID))
        if (!allowOverwrite && testVersionExists("speech", speechID))
            throw new EditorFormattingException("This speech exists already and isn't allowed to be overwritten");

        // TODO remove testing
//        mongoDBHandler.insertSpeech(uima.processSpeech(new Speech_Impl(protocolID, speakerID, text, date)));
        insertIntoTest(uima.processSpeech(new Speech_Impl(protocolID, speakerID, text, date)));
        // TODO remove testing
//        for (Comment comment : comments) mongoDBHandler.insertComment(comment, uima.getAverageSentiment(uima.getJCas(comment.getText())));
        insertIntoTest(comments);
        return speechID;
    }

    /**
     *
     * @param rawText
     * @param allowOverwrite
     * @return
     * @throws EditorFormattingException
     * @author Eric Lakhter
     */
    public String parseEditorPerson(String rawText, boolean allowOverwrite) throws EditorFormattingException {
        if (rawText == null || rawText.isEmpty())
            throw new EditorFormattingException("Text area is empty");
        String[] lines = rawText.split("\n");
        String personID = null;

        Set<String> check = new HashSet<>(0);

        for (String line : lines) {

        }
//        String[] picture = producePictureUrl();
        return personID;
    }

    /**
     * Checks whether a given ID can be inserted into the database.
     *
     * @param id Protocol ID, format: {@code x/y}.
     * @return If successful, returns {@code int[2]} containing the election period and protocol number.
     * @throws EditorFormattingException if at any point the ID appears to be unviable.
     * @author Eric Lakhter
     */
    private int[] validateProtocolID(String id) throws EditorFormattingException {
        try {
            String[] periodAndProtocol = id.split("/");
            if (periodAndProtocol.length != 2)
                throw new EditorFormattingException("Protocol ID can only have 1 '/' character");
            return new int[]{Integer.parseInt(periodAndProtocol[0]), Integer.parseInt(periodAndProtocol[1])};
        } catch (NumberFormatException e) {
            throw new EditorFormattingException("Protocol ID can only contain integers divided by a slash");
        }
    }

    /**
     *
     * @param protocolID
     * @return
     * @author Eric Lakhter
     */
    public String getEditorProtocolFromDB(String protocolID) {
        Document protocolDoc = mongoDBHandler.getDocument("protocol", protocolID);
        if (protocolDoc == null) return "";
        StringBuilder protocolEditorText = new StringBuilder();
        protocolEditorText.append("[PROTOKOLL]").append(protocolDoc.getString("_id"));
        protocolEditorText.append("\n[DATUM]").append(mongoDateToGermanDate(protocolDoc.getDate("date")));
        protocolEditorText.append("\n[BEGINN]").append(mongoTimeToLocalTime(protocolDoc.getDate("beginTime")));
        protocolEditorText.append("\n[ENDE]").append(mongoTimeToLocalTime(protocolDoc.getDate("endTime")));
        protocolEditorText.append("\n[SITZUNGSLEITER]");

        StringBuilder leaderText = new StringBuilder();
        for (String speechID : protocolDoc.getList("sessionLeaders", String.class)) {
            leaderText.append(", ").append(speechID);
        }
        protocolEditorText.append(leaderText.substring(2));

        protocolEditorText.append("\n[TOPS]");
        StringBuilder agendaText = new StringBuilder();
        for (String speechID : protocolDoc.getList("agendaItems", String.class)) {
            agendaText.append(", ").append(speechID);
        }
        protocolEditorText.append(agendaText.substring(2));

        return protocolEditorText.toString();
    }

    /**
     *
     * @param agendaID
     * @return
     * @author Eric Lakhter
     */

    public String getEditorAgendaFromDB(String agendaID) {
        Document agendaDoc = mongoDBHandler.getDocument("agendaItem", agendaID);
        if (agendaDoc == null) return "";
        StringBuilder agendaEditorText = new StringBuilder();
        String[] agendaIDparts = agendaDoc.getString("_id").split("/");
        agendaEditorText.append("[PROTOKOLL]").append(agendaIDparts[0]).append("/").append(agendaIDparts[1]);
        agendaEditorText.append("\n[TOP]").append(agendaIDparts[2]);

        for (String subject : agendaDoc.getString("subject").split("\n")) {
            agendaEditorText.append("\n[INHALT]").append(subject);
        }

        agendaEditorText.append("\n[REDEIDS]");
        StringBuilder speechIDString = new StringBuilder();
        for (String speechID : agendaDoc.getList("speechIDs", String.class)) {
            speechIDString.append(", ").append(speechID);
        }
        agendaEditorText.append(speechIDString.substring(2));

        return agendaEditorText.toString();
    }

    /**
     *
     * @param speechID
     * @return
     * @author Eric Lakhter
     */
    public String getEditorSpeechFromDB(String speechID) {
        Document speechDoc = mongoDBHandler.getDocument("speech", speechID);
        if (speechDoc == null) return "";
        MongoCursor<Document> commentCursor = mongoDBHandler.getDB().getCollection("comment")
                .aggregate(Arrays.asList(
                        match(new Document("speechID", speechID)),
                        new Document("$addFields", new Document("commentNum", new Document("$toInt", new Document("$arrayElemAt", asList(new Document("$split", asList("$_id", "/")), 1))))),
                        sort(ascending("commentNum"))
                )).iterator();

        int offSet = 0;
        int previousPos = 0;
        int currentPos = 0;
        StringBuilder speechEditorText = new StringBuilder(speechDoc.getString("text"));

        for (Document commentDoc = commentCursor.tryNext(); commentDoc != null; commentDoc = commentCursor.tryNext()) {
            currentPos = commentDoc.getInteger("commentPos");
            if (previousPos > currentPos || speechEditorText.length() < currentPos + offSet) break;

            String commentText = commentDoc.getString("text");
            speechEditorText.insert(currentPos + offSet, "\n[KOMMENTAR]" + commentText + "\n");
            previousPos = currentPos;
            offSet += commentText.length() + 13; // length of "\n[KOMMENTAR]"
        }
        return speechEditorText.toString();
    }

    /**
     *
     * @param personID
     * @return
     * @author Eric Lakhter
     */
    public String getEditorPersonFromDB(String personID) {
        Document personDoc = mongoDBHandler.getDocument("person", personID);
        if (personDoc == null) return "";
        StringBuilder personEditorText = new StringBuilder();

        personEditorText.append("[ID]").append(personDoc.getString("_id"));
        personEditorText.append("\n[VORNAME]").append(personDoc.getString("_id"));
        personEditorText.append("\n[NACHNAME]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ROLLE]").append(personDoc.getString("_id"));
        personEditorText.append("\n[TITEL]").append(personDoc.getString("_id"));
        personEditorText.append("\n[FRAKTION19]").append(personDoc.getString("_id"));
        personEditorText.append("\n[FRAKTION20]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ORTSZUSATZ]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ID]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ID]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ID]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ID]").append(personDoc.getString("_id"));
        personEditorText.append("\n[ID]").append(personDoc.getString("_id"));
        return personEditorText.toString();
    }

    // FOR TESTING

    @Testing
    private void insertIntoTest(Object insertObject) {
//        if (insertObject instanceof Protocol_Impl) {
//            Protocol_Impl protocol = (Protocol_Impl) insertObject;
//            mongoDBHandler.getDB().getCollection("editor_test_protocol")
//                    .updateOne(new Document("_id", protocol.getID()), new Document("_id", protocol.getID())
//                            .append("beginTime", protocol.getBeginTime())
//                            .append("endTime", protocol.getEndTime())
//                            .append("date", protocol.getDate())
//                            .append("duration", protocol.getDuration())
//                            .append("electionPeriod", protocol.getElectionPeriod())
//                            .append("protocolNumber", protocol.getProtocolNumber())
//                            .append("sessionLeaders", protocol.getSessionLeaders())
//                            .append("agendaItems", protocol.getAgendaItemIDs()), uo);
//        }
//
//        else if (insertObject instanceof AgendaItem_Impl) {
//            AgendaItem_Impl aItem = (AgendaItem_Impl) insertObject;
//            mongoDBHandler.getDB().getCollection("editor_test_agendaItem")
//                    .updateOne(new Document("_id", aItem.getID()), new Document(new Document("_id", aItem.getID())
//                            .append("date", aItem.getDate())
//                            .append("subject", aItem.getSubject())
//                            .append("speechIDs", aItem.getSpeechIDs())), uo);
//        }
//
//        else if (insertObject instanceof ProcessedSpeech) {
//            ProcessedSpeech processedSpeech = (ProcessedSpeech) insertObject;
//            try {
//                mongoDBHandler.getDB()
//                        .getCollection("editor_test_speech")
//                        .updateOne(new Document("_id", processedSpeech.getID()),
//                                Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()), uo);
//            } catch (MongoException | IllegalArgumentException ignored) {}
//            try {
//                mongoDBHandler.getDB()
//                        .getCollection("editor_test_speech_cas")
//                        .updateOne(new Document("_id", processedSpeech.getID()),
//                                new Document("_id", processedSpeech.getID()).append("fullCas", processedSpeech.getFullCas()), uo);
//            } catch (MongoException | IllegalArgumentException ignored) {}
//            try {
//                mongoDBHandler.getDB()
//                        .getCollection("editor_test_speech_tokens")
//                        .updateOne(new Document("_id", processedSpeech.getID()),
//                                Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()), uo);
//            } catch (MongoException | IllegalArgumentException ignored) {}
//        }
//
//        else if (insertObject instanceof List) {
//            List<Comment> comments = (List<Comment>) insertObject;
//            for (Comment comment : comments) {
//                double sentiment = uima.getAverageSentiment(uima.getJCas(comment.getText()));
//                mongoDBHandler.getDB().getCollection("editor_test_comment")
//                        .updateOne(new Document("_id", comment.getID()), new Document("_id", comment.getID())
//                                .append("speechID", comment.getSpeechID())
//                                .append("speakerID", comment.getSpeakerID())
//                                .append("commentatorID", comment.getCommentatorID())
//                                .append("commentPos", comment.getCommentPosition())
//                                .append("text", comment.getText())
//                                .append("date", comment.getDate())
//                                .append("sentiment", sentiment), uo);
//            }
//        }
//
//        else if (insertObject instanceof Person_Impl) {
//            Person_Impl person = (Person_Impl) insertObject;
//        }
    }
    @Testing
    private boolean testVersionExists(String col, String id) {
        return mongoDBHandler.getDB()
                .getCollection("editor_test_" + col)
                .find(new Document("_id", id))
                .iterator()
                .hasNext();
    }
}
