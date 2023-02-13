package utility.webservice;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import data.Comment;
import data.Person;
import data.impl.*;
import exceptions.EditorException;
import exceptions.WrongInputException;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.json.JSONObject;
import utility.MongoDBHandler;
import utility.UIMAPerformer;
import utility.annotations.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
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
     * @throws EditorException if any part of the text does
     *                                   not fit the standards for editing/creating protocols.
     * @author Eric Lakhter
     */
    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorProtocol(JSONObject protocolObject, boolean allowOverwrite) throws EditorException, WrongInputException {
        if (!protocolObject.keySet().containsAll(protocolReqs))
            throw new EditorException("Es wurden nicht alle notwendigen Informationen übergeben");

        String protocolID = protocolObject.getString("protocolID").trim();
//        if (!protocolObject.getBoolean("allowOverwrite") && mongoDBHandler.checkIfDocumentExists("protocol", id)) // TODO remove testing
        if (!allowOverwrite && testVersionExists("protocol", protocolID))
            throw new EditorException("Das Protokoll mit dieser ID existiert bereits und darf nicht überschrieben werden");
        int[] periodAndProtocol = validateProtocolID(protocolID);
        int electionPeriod = periodAndProtocol[0];
        int protocolNumber = periodAndProtocol[1];

        LocalDate date = convertToISOdate(protocolObject.getString("date").trim(), 3);
        LocalTime begin = convertToISOtime(protocolObject.getString("begin").trim());
        LocalTime end = convertToISOtime(protocolObject.getString("end").trim());

        Set<String> sessionLeaders = new HashSet<>(asList(protocolObject.getString("leaders").trim().split(",\\s*")));
        if (sessionLeaders.isEmpty())
            throw new EditorException("The session needs at least one leader");

        String[] agendaNames = protocolObject.getString("aItems").trim().split(",\\s*");
        if (agendaNames.length == 0)
            throw new EditorException("The session needs at least one agenda item");
        for (int i = 0; i < agendaNames.length; i++) {
            agendaNames[i] = protocolID + "/" + agendaNames[i];
        }
        ArrayList<String> agendaIDs = new ArrayList<>(asList(agendaNames));

        insertIntoTest(new Protocol_Impl(protocolID, date, begin, end, durationBetweenTimesInMinutes(begin, end),
                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));

//        mongoDBHandler.insertProtocol(new Protocol_Impl(id, date, begin, end, durationBetweenTimesInMinutes(begin, end), // TODO remove testing
//                electionPeriod, protocolNumber, sessionLeaders, agendaIDs));
        return protocolID;
    }

    /**
     * Parses a String and tries to convert it into a viable {@link AgendaItem_Impl}.
     * @param agendaObject JSON with at least all required agenda data.
     * @param allowOverwrite if true, allows overwriting the agenda item with the same ID in the database.
     * @return The ID of the freshly inserted agenda item.
     * @throws EditorException if any part of the text does
     *                                   not fit the standards for editing/creating protocols.
     * @author Eric Lakhter
     */
    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorAgendaItem(JSONObject agendaObject, boolean allowOverwrite) throws EditorException, WrongInputException {
        if (!agendaObject.keySet().containsAll(agendaReqs))
            throw new EditorException("Es wurden nicht alle notwendigen Informationen übergeben");

        String protocolID = agendaObject.getString("protocolID").trim();
        validateProtocolID(protocolID);
        String agendaID = agendaObject.getString("protocolID").trim();
        String fullAgendaID = protocolID + "/" + agendaID;
//        if (!allowOverwrite && mongoDBHandler.checkIfDocumentExists("agendaItem", fullAgendaID))   // TODO remove testing
        if (!allowOverwrite && testVersionExists("agendaItem", fullAgendaID))
            throw new EditorException("Der Tagesordnungspunkt mit dieser ID existiert bereits und darf nicht überschrieben werden");

        String subject = agendaObject.getString("subject").trim();

        ArrayList<String> speechIDs = new ArrayList<>(asList(agendaObject.getString("speechIDs").trim().split(",\\s*")));
        String speechPrefix = "ID" + protocolID.split("/")[0];
        for (String speechID : speechIDs) {
            if (!speechID.startsWith(speechPrefix))
                throw new EditorException("Alle Rede-IDs müssen mit dem gewählten Präfix \"" + speechPrefix + "\" beginnen");
        }

        LocalDate date = dateToLocalDate(mongoDBHandler.getDocumentIfExists("protocol", protocolID).getDate("date"));

//        mongoDBHandler.insertAgendaItems(Collections.singletonList(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs)));   // TODO remove testing
        insertIntoTest(new AgendaItem_Impl(fullAgendaID, date, subject, speechIDs));
        return fullAgendaID;
    }

    /**
     * Parses a String and tries to convert it into a viable {@link Speech_Impl}.
     * @param speechObject JSON with at least all required speech data.
     * @param allowOverwrite if true, allows overwriting the speech with the same ID in the database.
     * @return The ID of the freshly inserted speech.
     * @throws EditorException if any part of the text does
     *                                   not fit the standards for editing/creating protocols.
     * @author Eric Lakhter
     */
    @Unfinished("Pretty much finished, but inserts into a test collection")
    public String parseEditorSpeech(JSONObject speechObject, boolean allowOverwrite) throws EditorException, WrongInputException {
        if (!speechObject.keySet().containsAll(speechReqs))
            throw new EditorException("Es wurden nicht alle notwendigen Informationen übergeben");

        String speechID = speechObject.getString("speechID").trim();
//        if (!allowOverwrite && mongoDBHandler.checkIfDocumentExists("agendaItem", fullAgendaID))    // TODO remove testing
        if (!allowOverwrite && testVersionExists("speech", speechID))
            throw new EditorException("Die Rede mit dieser ID existiert bereits und darf nicht überschrieben werden");
        Document relatedAgenda = mongoDBHandler.getDB().getCollection("agendaItem")
                .aggregate(asList(
                        lookup("speech", "speechIDs", "_id", "speeches"),
                        unwind("$speeches"),
                        match(eq("speeches._id", speechID)))).first();
        if (relatedAgenda == null)
            throw new EditorException("Kein Tagesordnungspunkt enthält diese Rede");
        LocalDate date = dateToLocalDate(relatedAgenda.getDate("date"));

        String speakerID = speechObject.getString("speakerID").trim();
        if (mongoDBHandler.getDocumentIfExists("person", speakerID) == null)
            throw new EditorException("Der Redner mit ID " + speakerID + " existiert nicht");

        String[] lines = speechObject.getString("text").trim().split("\n");
        StringBuilder speechText = new StringBuilder();
        List<Comment> comments = new ArrayList<>();
        ArrayList<Person_Impl> personList = mongoDBHandler.getPersons();

        int commentCounter = 1;
        for (String line : lines) {
            if (line.startsWith("[KOMMENTAR]")) {
                commentCounter++;
                String commentText = line.substring(11).trim();
                String commentatorID = "";
                for (Person person : personList) {
                    String fullName = person.getFullName();
                    if (commentText.indexOf(fullName) < commentText.indexOf("[") && commentText.contains(fullName)) {
                        commentatorID = person.getID();
                        break;
                    }
                }
                comments.add(new Comment_Impl(speechID + "/" + commentCounter, speechID, speakerID, commentatorID, speechText.length(), commentText, date, new ArrayList<>()));
            } else {
                speechText.append(" ").append(line.trim());
            }
        }

        if (speechText.length() == 0)
            throw new EditorException("Die Rede enthält keinen Text");

//        mongoDBHandler.insertSpeech(uima.processSpeech(new Speech_Impl(protocolID, speakerID, text, date)));        // TODO remove testing
        insertIntoTest(uima.processSpeech(new Speech_Impl(speechID, speakerID, speechText.toString(), date)));
//        for (Comment comment : comments) mongoDBHandler.insertComment(comment, uima.getAverageSentiment(uima.getJCas(comment.getText())));        // TODO remove testing
        insertIntoTest(comments);
        return speechID;
    }

    /**
     *
     * @param personObject JSON with at least all required person data.
     * @param allowOverwrite if true, allows overwriting the person with the same ID in the database.
     * @return
     * @throws EditorException
     * @author Eric Lakhter
     */
    public String parseEditorPerson(JSONObject personObject, boolean allowOverwrite) throws EditorException {

        if (!personObject.keySet().containsAll(personReqs))
            throw new EditorException("Es wurden nicht alle notwendigen Informationen übergeben");

        String personID = personObject.getString("personID");

        String firstName = personObject.getString("firstName");
        String lastName = personObject.getString("lastName");
        String role = personObject.getString("role");
        String title = personObject.getString("title");
        String place = personObject.getString("place");
        String party = personObject.getString("party");
        String fraction19 = personObject.getString("fraction19");
        String fraction20 = personObject.getString("fraction20");
        String gender = personObject.getString("gender");
        String birthDate = personObject.getString("birthDate");
        String deathDate = personObject.getString("deathDate");
        String birthPlace = personObject.getString("birthPlace");

        String[] picture = producePictureUrl(firstName, lastName);
        return personID;
    }

    /**
     * Checks whether a given ID can be inserted into the database.
     *
     * @param id Protocol ID, format: {@code x/y}.
     * @return If successful, returns {@code int[2]} containing the election period and protocol number.
     * @throws EditorException if at any point the ID appears to be unviable.
     * @author Eric Lakhter
     */
    private int[] validateProtocolID(String id) throws EditorException {
        try {
            String[] periodAndProtocol = id.split("/");
            if (periodAndProtocol.length != 2)
                throw new EditorException("Protokoll-ID muss genau 1 '/' haben");
            return new int[]{Integer.parseInt(periodAndProtocol[0]), Integer.parseInt(periodAndProtocol[1])};
        } catch (NumberFormatException e) {
            throw new EditorException("Protokoll-ID kann nur aus zwei ganzen Zahlen getrennt von einem Slash bestehen");
        }
    }

    /**
     *
     * @param protocolID
     * @return
     * @author Eric Lakhter
     */
    public JSONObject getEditorProtocolFromDB(String protocolID) throws EditorException {
        Document protocolDoc = mongoDBHandler.getDocumentIfExists("protocol", protocolID);
        if (protocolDoc == null) throw new EditorException("Es existiert kein Protokoll mit ID " + protocolID);

        JSONObject protocol = new JSONObject();
        protocol.put("protocolID", protocolDoc.getString("_id"));
        protocol.put("date", dateToLocalDate(protocolDoc.getDate("date")));
        protocol.put("begin", dateToLocalTime(protocolDoc.getDate("beginTime")));
        protocol.put("end", dateToLocalTime(protocolDoc.getDate("endTime")));
        protocol.put("leaders", String.join(", ", protocolDoc.getList("sessionLeaders", String.class)));
        protocol.put("aItems", String.join(", ", protocolDoc.getList("agendaItems", String.class)));
        return protocol;
    }

    /**
     *
     * @param agendaID
     * @return
     * @author Eric Lakhter
     */

    public JSONObject getEditorAgendaFromDB(String agendaID) throws EditorException {
        Document agendaDoc = mongoDBHandler.getDocumentIfExists("agendaItem", agendaID);
        if (agendaDoc == null) throw new EditorException("Es existiert kein Tagesordnungspunkt mit ID " + agendaID);

        JSONObject aItem = new JSONObject();
        String[] agendaIDparts = agendaDoc.getString("_id").split("/");
        aItem.put("protocolID", agendaIDparts[0] + "/" + agendaIDparts[1]);
        aItem.put("agendaID", agendaIDparts[2]);
        aItem.put("subject", agendaDoc.getString("subject"));
        aItem.put("speechIDs", String.join(", ", agendaDoc.getList("speechIDs", String.class)));

        return aItem;
    }

    /**
     *
     * @param speechID
     * @return
     * @author Eric Lakhter
     */
    public JSONObject getEditorSpeechFromDB(String speechID) throws EditorException {
        Document speechDoc = mongoDBHandler.getDocumentIfExists("speech", speechID);
        if (speechDoc == null) throw new EditorException("Es existiert keine Rede mit ID " + speechID);

        JSONObject speech = new JSONObject();
        speech.put("speechID", speechDoc.getString("_id"));
        speech.put("speakerID", speechDoc.getString("speakerID"));

        MongoCursor<Document> commentCursor = mongoDBHandler.getDB().getCollection("comment")
                .aggregate(Arrays.asList(
                        match(new Document("speechID", speechID)),
                        new Document("$addFields", new Document("commentNum", new Document("$toInt", new Document("$arrayElemAt", asList(new Document("$split", asList("$_id", "/")), 1))))),
                        sort(ascending("commentNum"))
                )).iterator();
        int offSet = 0;
        int previousPos = 0;
        int currentPos;
        StringBuilder speechEditorText = new StringBuilder(speechDoc.getString("text"));
        for (Document commentDoc = commentCursor.tryNext(); commentDoc != null; commentDoc = commentCursor.tryNext()) {
            currentPos = commentDoc.getInteger("commentPos");
            if (previousPos > currentPos || speechEditorText.length() < currentPos + offSet) break;

            String commentText = commentDoc.getString("text");
            speechEditorText.insert(currentPos + offSet, "\n[KOMMENTAR]" + commentText + "\n");
            previousPos = currentPos;
            offSet += commentText.length() + 13; // length of "\n[KOMMENTAR]"
        }
        speech.put("text", speechEditorText.toString().replace("\n ", "\n"));
        return speech;
    }

    /**
     *
     * @param personID
     * @return
     * @author Eric Lakhter
     */
    public JSONObject getEditorPersonFromDB(String personID) throws EditorException {
        Document personDoc = mongoDBHandler.getDocumentIfExists("person", personID);
        if (personDoc == null) throw new EditorException("Es existiert keine Person mit ID " + personID);

        JSONObject person = new JSONObject(personDoc.toJson())
                .put("personID", personDoc.getString("_id"))
                .put("birthDate", dateToLocalDate(personDoc.getDate("birthDate")))
                .put("deathDate", dateToLocalDate(personDoc.getDate("deathDate")));
        return person;
//        JSONObject person = new JSONObject();
//        person.put("personID", personDoc.getString("_id"));
//        person.put("firstName", personDoc.getString("firstName"));
//        person.put("lastName", personDoc.getString("lastName"));
//        person.put("role", personDoc.getString("role"));
//        person.put("title", personDoc.getString("title"));
//        person.put("place", personDoc.getString("place"));
//        person.put("party", personDoc.getString("party"));
//        person.put("fraction19", personDoc.getString("fraction19"));
//        person.put("fraction20", personDoc.getString("fraction20"));
//        person.put("gender", personDoc.getString("gender"));
//        person.put("birthDate", personDoc.getString("birthDate"));
//        person.put("deathDate", personDoc.getString("deathDate"));
//        person.put("birthPlace", personDoc.getString("birthPlace"));
//        return person;
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
                .first() != null;
    }
}
