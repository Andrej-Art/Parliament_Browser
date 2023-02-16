package utility;

import com.google.gson.Gson;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import data.*;
import data.impl.AgendaItem_Impl;
import data.impl.Person_Impl;
import exceptions.WrongInputException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utility.annotations.*;
import utility.uima.ProcessedSpeech;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static utility.TimeHelper.dateToLocalDate;

/**
 * When instanced, the {@code MongoDBHandler} connects to the MongoDB specified in {@code PRG_WiSe22_Group_9_4.txt}.
 * All methods which manipulate or query data in the database are found here.
 *
 * @author Eric Lakhter
 * @author DavidJordan
 */
@Unfinished("Needs a few more web-related methods")
public class MongoDBHandler {
    private static MongoDBHandler mongoDBHandler;

    static {
        try {
            mongoDBHandler = new MongoDBHandler();
        } catch (Exception e) {
            mongoDBHandler = null;
        }
    }

    private final MongoDatabase db;
    private final InsertManyOptions imo = new InsertManyOptions().ordered(false);
    private final Gson gson = new Gson();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    /**
     * Connects to the MongoDB specified in {@code PRG_WiSe22_Group_9_4.txt}.
     *
     * @author Eric Lakhter
     */
    public MongoDBHandler() throws IOException {
        Properties prop = new Properties();
        prop.load(Files.newInputStream(Paths.get("src/main/resources/PRG_WiSe22_Group_9_4.txt")));
        MongoClient client = MongoClients.create(
                "mongodb://" + prop.getProperty("remote_user") +
                        ":" + prop.getProperty("remote_password") +
                        "@" + prop.getProperty("remote_host") +
                        ":" + prop.getProperty("remote_port") +
                        "/?authSource=" + prop.getProperty("remote_user"));
        db = client.getDatabase(prop.getProperty("remote_database"));
    }

    /**
     * Returns the instance of the {@code MongoDBHandler}.
     *
     * @return Singleton instance of {@code MongoDBHandler}.
     * @author Eric Lakhter
     */
    public static MongoDBHandler getHandler() {
        return mongoDBHandler;
    }

    /**
     * This method enables other classes to access the database for testing.
     *
     * @return The connected database.
     * @author Eric Lakhter
     */
    @Testing
    public MongoDatabase getDB() {
        return db;
    }

    /**
     * Basic method to check whether a given collection already exists in the Database
     *
     * @param col collection name
     * @return true if it exists
     * @author DavidJordan
     */
    public boolean collectionExists(String col) {
        for (String colName : db.listCollectionNames()) {
            if (colName.equals(col)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets a Document with the specified id from the specified collection
     *
     * @param col the collection name
     * @param id  the _id of the specified Document
     * @return the found Document
     * @author DavidJordan
     */
    public Document getDocument(String col, String id) {
        Document document = new Document();
        try {
            Document queryDoc = new Document().append("_id", id);
            for (Document value : db.getCollection(col).find(queryDoc)) {
                document = value;
            }
        } catch (Exception e) {
            System.err.println("Document was not found in the collection.");
        }
        return document;
    }

    /**
     * Returns either a document if the ID matches a document in the target collection or {@code null}.
     *
     * @param col Target collection.
     * @param id  ID to search.
     * @return Either a document or {@code null}.
     * @author Eric Lakhter
     */
    public Document getDocumentOrNull(String col, String id) {
        return db.getCollection(col).find(new Document("_id", id)).first();
    }

    /**
     * Basic Method to create a collection
     *
     * @param col collection name
     * @author DavidJordan
     */
    public void createCollection(String col) {
        if (!collectionExists(col)) {
            db.createCollection(col);
            System.out.println("Collection " + col + "was created.");
        } else {
            System.out.println("Collection " + col + "was not created because it already exists.");
        }
    }

    /**
     * Method to delete a Document with specified id in a specified collection.
     *
     * @param col the target collection's name
     * @param id  the id of the document to delete
     * @author David Jordan
     */
    public void deleteDocument(String col, String id) {
        if (checkIfDocumentExists(col, id)) {
            db.getCollection(col).deleteOne(Filters.eq("_id", id));
        } else {
            System.out.println("Document with _id: \"" + "\" was not found in collection: " + col);
        }
    }

    /**
     * Method to convert a List of Java Person_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     *
     * @param persons the List of Persons to add to the database
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertPersons(List<Person> persons) throws WrongInputException {
        if (persons == null || persons.isEmpty()) {
            throw new WrongInputException("Input is null or empty.");
        }
        ArrayList<Document> mongoPersons = new ArrayList<>(0);
        for (Person person : persons) {
            if (person == null) continue;
            mongoPersons.add(Document.parse(gson.toJson(person)));
        }
        try {
            db.getCollection("person").insertMany(mongoPersons);
        } catch (MongoException | IllegalArgumentException ignored) {
        }
    }

    /**
     * Method to insert a single Person object into the database in serialised form
     *
     * @param person The Person Object
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertPerson(Person person) throws WrongInputException {
        if (person == null) {
            throw new WrongInputException("person is null. Input failed.");
        }
        Document personDoc = Document.parse(gson.toJson(person));

        try {
            db.getCollection("person").insertOne(personDoc);
        } catch (MongoException e) {
            System.err.println("Insert of person failed.");
        }
    }

    /**
     * Method to insert a single Protocol into the database in serialized form.
     *
     * @param protocol The Protocol Object to be inserted
     * @throws WrongInputException
     */
    public void insertProtocol(Protocol protocol) throws WrongInputException {
        if (protocol == null) {
            throw new WrongInputException("Protocol is null. Input failed.");
        }
        Document protocolDoc = new Document("_id", protocol.getID())
                .append("beginTime", protocol.getBeginTime())
                .append("endTime", protocol.getEndTime())
                .append("date", protocol.getDate())
                .append("duration", protocol.getDuration())
                .append("electionPeriod", protocol.getElectionPeriod())
                .append("protocolNumber", protocol.getProtocolNumber())
                .append("sessionLeaders", protocol.getSessionLeaders())
                .append("agendaItems", protocol.getAgendaItemIDs());

        try {
            db.getCollection("protocol").insertOne(protocolDoc);
        } catch (MongoException e) {
            System.err.println("Insert of protocol failed.");
        }
    }

    /**
     * Method to insert a list of Protocol Object into the database in serialized form.
     *
     * @param protocols the ArrayList of Protocol Objects
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertProtocols(List<Protocol> protocols) throws WrongInputException {
        if (protocols == null || protocols.isEmpty()) {
            throw new WrongInputException("Input is null or empty.");
        }
        ArrayList<Document> protocolDocs = new ArrayList<>(0);
        for (Protocol protocol : protocols) {
            if (protocol == null) {
                throw new IllegalArgumentException("protocol is null.");
            }
            protocolDocs.add(Document.parse(gson.toJson(protocol)));
        }

        try {
            db.getCollection("protocol").insertMany(protocolDocs);
        } catch (MongoException e) {
            System.err.println("Insert of protocols failed.");
        }
    }

    /**
     * Method to insert a User
     *
     * @param id       The Username of the User
     * @param password The password of the user
     * @param rights   The rights that the user has when using his account
     * @author DavidJordan
     */
    public void insertUser(String id, String password, String rights) {
        //Create the user Document
        Document userDoc = new Document()
                .append("_id", id)
                .append("password", password)
                .append("rights", rights);
        // Insert it into the DB
        try {
            db.getCollection("user").insertOne(userDoc);
        } catch (MongoException | IllegalArgumentException e) {
            System.err.println("User: " + id + " could not be inserted.");
        }
    }

    /**
     * This Method pulls all the persons from DB and returns them as a ArrayList of Person_Impl
     *
     * @return
     * @author Julian Ocker
     */
    public ArrayList<Person_Impl> getPersons() {
        ArrayList<Person_Impl> personsList = new ArrayList<>(0);
        db.getCollection("person").find().forEach((Consumer<? super Document>) procBlock -> personsList.add(new Person_Impl(procBlock)));
        return personsList;
    }


    /**
     * Method to insert a single ProcessedSpeech Object into the database. Inserts a different representation
     * into speech, speech_cas, and speech_tokens  collections respectively.
     *
     * @param processedSpeech
     * @author DavidJordan
     */
    public void insertSpeech(ProcessedSpeech processedSpeech) {
        //Insert single processedSpeech into speech collection
        try {
            db.getCollection("speech").insertOne(Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()));
        } catch (MongoException | IllegalArgumentException ignored) {
        }

        //Insert single document into speech_cas collection
        try {
            db.getCollection("speech_cas").insertOne(new Document("_id", processedSpeech.getID()).append("fullCas", processedSpeech.getFullCas()));
        } catch (MongoException | IllegalArgumentException ignored) {
        }

        //Insert single document into speech_tokens collection
        try {
            db.getCollection("speech_tokens").insertOne(Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()));
        } catch (MongoException | IllegalArgumentException ignored) {
        }
    }

    /**
     * Inserts a list of processed speeches into the DB.
     *
     * @param processedSpeeches List of processed speeches.
     * @author DavidJordan
     * @author Eric_Lakhter
     */
    public void insertSpeeches(List<ProcessedSpeech> processedSpeeches) {

        List<Document> speechDocs = new ArrayList<>(0);
        List<Document> speechCasDocs = new ArrayList<>(0);
        List<Document> speechTokenDocs = new ArrayList<>(0);

        for (ProcessedSpeech processedSpeech : processedSpeeches) {
            //parse into Bson Document and add to list
            speechDocs.add(Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()));

            //parse into Bson Document for speech_cas and add to list
            speechCasDocs.add(new Document("_id", processedSpeech.getID()).append("fullCas", processedSpeech.getFullCas()));

            //parse into Bson Document for speech_tokens collection and add to list
            speechTokenDocs.add(Document.parse(processedSpeech.toSpeechTokensJson()).append("date", processedSpeech.getDate()));
        }

        // MongoBulkWriteExceptions are caught when inserting the Lists
        try {
            db.getCollection("speech").insertMany(speechDocs, imo);
        } catch (MongoException | IllegalArgumentException ignored) {
        }
        try {
            db.getCollection("speech_cas").insertMany(speechCasDocs, imo);
        } catch (MongoException | IllegalArgumentException ignored) {
        }
        try {
            db.getCollection("speech_token").insertMany(speechTokenDocs, imo);
        } catch (MongoException | IllegalArgumentException ignored) {
        }

    }

    /**
     * Method to convert a List of Java AgendaItem_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     *
     * @param agendaItems the List of
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertAgendaItems(List<AgendaItem_Impl> agendaItems) throws WrongInputException {
        if (agendaItems == null || agendaItems.isEmpty()) {
            throw new WrongInputException("Input is null or empty");
        }
        ArrayList<Document> mongoAgendaItems = new ArrayList<>(0);
        for (AgendaItem agendaItem : agendaItems) {
            if (agendaItem == null) continue;
            mongoAgendaItems.add(new Document("_id", agendaItem.getID())
                    .append("date", agendaItem.getDate())
                    .append("subject", agendaItem.getSubject())
                    .append("speechIDs", agendaItem.getSpeechIDs()));
        }
        try {
            db.getCollection("agendaItem").insertMany(mongoAgendaItems);
        } catch (MongoException | IllegalArgumentException ignored) {
        }
    }


    /**
     * Method to insert a comment and its sentiment value into the "comment" collection
     * of the database.
     *
     * @param comment   The comment Object
     * @param sentiment The sentiment value of the comment
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertComment(Comment comment, double sentiment) throws WrongInputException {
        if (comment == null) {
            throw new WrongInputException("comment is null");
        }
        //Create the  Comment Document
        Document commentDoc = new Document()
                .append("_id", comment.getID())
                .append("speechID", comment.getSpeechID())
                .append("speakerID", comment.getSpeakerID())
                .append("commentatorID", comment.getCommentatorID())
                .append("commentPos", comment.getCommentPosition())
                .append("text", comment.getText())
                .append("date", comment.getDate())
                .append("sentiment", sentiment);
        // Insert it into the comment collection

        try {
            db.getCollection("comment").insertOne(commentDoc);
        } catch (MongoException | IllegalArgumentException e) {
            System.err.println("Insert of comment with id: " + comment.getID() + "could not be performed.");
        }
    }

    /**
     * Inserts a list of polls into the {@code poll} collection.
     *
     * @param polls List of Poll objects.
     * @author Eric Lakhter
     */
    public void insertPolls(List<Poll> polls) {
        List<Document> pollDocs = new ArrayList<>();
        for (Poll poll : polls) {
            if (poll != null) pollDocs.add(Document.parse(poll.toJson()).append("date", poll.getDate()));
        }
        try {
            db.getCollection("poll").insertMany(pollDocs, imo);
        } catch (MongoException | IllegalArgumentException ignored) {
        }
    }

    /**
     * Updates a Document in the database, by replacing it with the given Document
     *
     * @param document   the document that will replace the Document with the specified id
     * @param collection the collection to get from the database
     * @param id         the id of the Document to be replaced
     * @return boolean true if update was successful, false if not
     * @author DavidJordan
     */
    public boolean updateDocument(Document document, String collection, String id) {

        if (!checkIfDocumentExists(collection, id)) {
            System.out.println("Unable to perform update, because the target Document with id: " + id + " does not exist in col: " + collection);
            return false;
        } else {
            try {
                db.getCollection(collection).replaceOne(eq("_id", id), document);
                return true;
            } catch (Exception e) {
                System.err.println("Update could not be performed. Invalid input.");
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Adds a document to an existing collection
     *
     * @param document   The Bson Document to be added
     * @param collection The collection name of where it is supposed to be added
     * @author DavidJordan
     */
    public void addDocument(Document document, String collection) {
        if (collectionExists(collection)) {
            try {
                db.getCollection(collection).insertOne(document);
            } catch (MongoException e) {
                System.err.println("Failed to add Document to collection.");
            }
        }
    }

    /**
     * Adds a documents to an existing collection
     *
     * @param documents
     * @param collection
     * @author DavidJordan
     */
    public void addDocuments(String collection, List<Document> documents) {
        if (collectionExists(collection) && documents != null) {
            try {
                db.getCollection(collection).insertMany(documents);
            } catch (MongoException | IllegalArgumentException e) {
                System.err.println("Failed to add Documents to collection.");
            }
        }
    }

    /**
     * Verifies whether a person in the DB has real picture data or a null array.<br>
     * If the array is null, tries to update its contents with actual data.
     *
     * @author Eric Lakhter
     */
    public void checkPersonPictureData() {
        for (Document doc : db.getCollection("person").find()) {
            if (((ArrayList<String>) doc.get("picture")).get(0) == null) {
                String id = doc.getString("_id");
                String firstName = doc.getString("firstName");
                String lastName = doc.getString("lastName");
                String[] pictureData = PictureScraper.producePictureUrl(firstName, lastName);
                List<String> picture = new ArrayList<>(Arrays.asList(pictureData));
                db.getCollection("person").updateOne(new Document("_id", id), new Document("$set", new Document("picture", picture)));
            }
        }
    }

    /**
     * Adds potential date filters in front of an aggregation pipeline.
     *
     * @param pipeline      The pipeline to be modified.
     * @param dateFilterOne If {@code (dateFilterTwo.isEmpty() == true)} this is a specific date,
     *                      else it's the lower bound for a date range to be filtered for.
     * @param dateFilterTwo Higher bound for dates to be filtered for. Gets ignored if {@code (dateFilterOne.isEmpty() == true)}.
     *                      <p>If {@code (dateFilterTwo < dateFilterOne)} then the query result will be empty.
     * @author Eric Lakhter
     * @modified DavidJordan
     * @modified Edvin Nise
     */
    public void
    applyDateFiltersToAggregation(List<Bson> pipeline, String dateFilterOne, String dateFilterTwo) {
        if (!dateFilterOne.isEmpty()) {
            LocalDate dateOne = TimeHelper.convertToISOdate(dateFilterOne, 3);
            LocalDate dateTwo = TimeHelper.convertToISOdate(dateFilterTwo, 3);
            Bson matchDate = dateFilterTwo.isEmpty() ?
                    match(new Document("date", dateFilterOne)) :
                    match(and(Arrays.asList(gte("date", dateOne), lte("date", dateTwo))));
            pipeline.add(0, matchDate);
        }
    }


    /**
     * Adds potential person/fraction filters in front of a pipeline performed on either the speech or comment collection.
     * If both person and fraction filters exist, person has priority while fraction gets ignored.
     * <p> If enabled, the project stage leaves the pipeline with a collection containing the
     * {@code _id, speechID, speakerID, neededFieldOne, neededFieldTwo} and if filtering for fractions a
     * {@code persondata} field matching the {@code speakerID} form the person collection.
     *
     * @param pipeline       The pipeline to be modified.
     * @param fractionFilter the fraction to be filtered for.
     * @param neededField    Potential field names to be added to the projection. No projection will be performed if
     *                       no needed fields are given.
     * @author Eric Lakhter
     * @modified DavidJordan
     */
    public void applyPersonFractionFiltersToAggregation(
            List<Bson> pipeline,
            String fractionFilter,
            String personFilter,
            String partyFilter,
            String... neededField) {
        Document projectDoc = new Document("speechID", 1).append("speakerID", 1);
        // Setting each of the needed fields to be included in the results
        for (String field : neededField) {
            projectDoc.append(field, 1);
        }

        Bson project = project(projectDoc);

        if (!personFilter.isEmpty()) {
            pipeline.add(0, match(new Document("personDaten.fullName", personFilter)));
            pipeline.add(0, unwind("$personDaten"));
            pipeline.add(0, lookup("person", "speakerID", "_id", "personDaten"));
        }
        if (!fractionFilter.isEmpty()) {
            pipeline.add(0, match(new Document("persondata.fraction19", fractionFilter)));
            pipeline.add(0, unwind("$persondata"));
            pipeline.add(0, lookup("person", "speakerID", "_id", "persondata"));
        }
        if (!partyFilter.isEmpty()) {
            pipeline.add(0, match(new Document("persondata.party", partyFilter)));
            pipeline.add(0, unwind("$persondata"));
            pipeline.add(0, lookup("person", "speakerID", "_id", "persondata"));
        }

        if (neededField.length != 0) {
            pipeline.add(0, project);
        }
    }

    /**
     * Adds potential sentiment filter to an aggregation pipeline. If no sentiment is set and no neededFields are given,
     * the pipeline is unaltered. If neededFields are provided they are added to a project stage and added to the beginning
     * of the pipeline. If a sentiment value   is given the filter is set accordingly.
     *
     * @param pipeline    the given aggregation pipeline
     * @param sentiment   a String which must be either:  "positive", "neutral" , "negative"  .  To set the filter for the sentiment field.
     * @param neededField a number of potential fields that the caller wants to set. No projection is performed if no neededFields are given.
     * @author DavidJordan
     */
    public void applySentimentFilterToAggregation(List<Bson> pipeline, String sentiment, String... neededField) {
        Document projectDoc = new Document();
        for (String field : neededField) {
            projectDoc.append(field, 1);
        }
        Bson project = project(projectDoc);

        Bson sentimentFilter = null;
        switch (sentiment) {
            case "negative":
                sentimentFilter = Filters.lt("sentiment", 0);
                break;
            case "neutral":
                sentimentFilter = Filters.eq("sentiment", 0);
                break;
            case "positive":
                sentimentFilter = Filters.gt("sentiment", 0);
                break;
        }
        if (sentimentFilter != null) {
            pipeline.add(0, match(sentimentFilter));
        }

        if (neededField.length != 0) {
            pipeline.add(project);
        }
    }

    /**
     * Method to apply a party filter to an aggregation pipeline. Needed fields are optional to provide.
     * If no party name is provided the pipeline is left unaltered. If no needed fields are provided no projection
     * is performed and the pipeline is also unaltered.
     *
     * @param pipeline    The aggregation pipeline to apply the filter to.
     * @param party       The String valued name of the party to filter for. Names may be chosen from the following list.
     *                    Mind the exact spelling: {"CDU", "CSU", "SPD", "DIE LINKE", "BÜNDNIS 90/DIE GRÜNEN", "FDP", "AfD"}
     * @param neededField Optionally provided fields to narrow the number of fields in the result to the specified fields.
     * @author DavidJordan
     */
    public void applyPartyFilterToAggregation(List<Bson> pipeline, String party, String... neededField) {
        Document projDoc = new Document();
        //Add fields to projection Document if present
        for (String field : neededField) {
            projDoc.append(field, 1);
        }
        Bson project = project(projDoc);

        Bson partyFilter = null;
        // Create the Filter for the specified party
        if (!party.isEmpty()) {
            partyFilter = Filters.eq("party", party);
        }
        // Add as a match stage to the beginning of the pipeline
        if (partyFilter != null) {
            pipeline.add(0, match(partyFilter));
        }

        if (neededField.length != 0) {
            pipeline.add(project);
        }

    }


    /**
     * A Method that executes an aggregation query on a target collection, according to the given filter
     * parameters. The pipeline is created within the method and the aggregation is run once all filters have been
     * added. Returns the complete Bson Documents which may then be used for instantiating Java Objects.
     *
     * @param collection      The name of the collection to aggregate
     * @param dateFilterOne   The first date-filter that is passed to the applyDateFilter...  method
     * @param dateFilterTwo   The second date-filter that is passed to the applyDateFilter... method
     * @param personFilter    The _id of the Person that is passed to the applyPersonFractionFilter... method
     * @param fractionFilter  The name of the fraction to be passed to the applyPersonFractionFilter... method
     * @param sentimentFilter The sentiment type (positive, neutral, negative) to be passed to the applySentimentFilter... method
     * @return MongoIterable
     * @author DavidJordan
     */

    public MongoIterable<Document> runAggregationQueryWithFilters(
            String collection,
            String dateFilterOne,
            String dateFilterTwo,
            String personFilter,
            String fractionFilter,
            String sentimentFilter,
            String partyFilter) {

        //create the pipeline
        List<Bson> pipeline = new ArrayList<>(0);

        //if datefilters are present add them to the pipeline
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        // if person or fraction filters are present, add them as well
        if (!personFilter.isEmpty() || !fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, personFilter, fractionFilter, partyFilter);
        }
        //if sentiment filter is provided add it
        if (!sentimentFilter.isEmpty()) {
            applySentimentFilterToAggregation(pipeline, sentimentFilter);
        }
        // Return an Iterable of the queried Documents
        return db.getCollection(collection).aggregate(pipeline);
    }


    /**
     * Returns the text belonging to either a speech or comment ID.
     *
     * @param col Collection to collect from. Must be either {@code speech} or {@code comment}.
     * @param id  The ID to look for.
     * @return {@code text} String.
     * @author Eric Lakhter
     */
    public String getText(String col, String id) throws NullPointerException {
        Document result = db.getCollection(col).find(new Document("_id", id)).iterator().tryNext();
        if (result == null)
            throw new NullPointerException("The Document with _id = " + id + " does not exist in this collection.");
        return result.getString("text");
    }

    /**
     * Adds a full CAS XML String to a collection.
     *
     * @param col Collection to insert in. Must be either {@code speech_cas} or {@code comment_cas}.
     * @param id  Speech/Comment ID.
     * @param cas The CAS String.
     * @author Eric Lakhter
     */
    public void addCAS(String col, String id, String cas) {
        db.getCollection(col).insertOne(new Document("_id", id).append("cas", cas));
    }

    /**
     * Checks if a Document specified by the {@code id} has a given {@code field}.
     *
     * @param col   Collection to search in.
     * @param id    Document key.
     * @param field Field name.
     * @return {@code true} if the field exists, {@code false} otherwise.
     * @author Eric Lakhter
     */
    public boolean checkIfHasNonEmptyField(String col, String id, String field) {
        return db.getCollection(col).find(and(new Document("_id", id), ne(field, null))).iterator().hasNext();
    }

    /**
     * Checks if a Document exists in a collection.
     *
     * @param col Collection to search in.
     * @param id  Document key.
     * @return {@code true} if the Document exists, {@code false} otherwise.
     * @author Eric Lakhter
     */
    public boolean checkIfDocumentExists(String col, String id) {
        return db.getCollection(col).find(new Document("_id", id)).iterator().hasNext();
    }

    /**
     * returns all Speakers with their speeches count.
     * @param dateFilterOne
     * @param dateFilterTwo
     * @author Edvin Nise
     */
    public ArrayList<JSONObject> getSpeechesBySpeakerCount(String dateFilterOne,
                                                           String dateFilterTwo,
                                                           String fractionFilter,
                                                           String partyFilter,
                                                           String personFilter,
                                                           Integer limiter) {
        Bson group = new Document("$group", new Document("_id", "$speakerID").append("speechesCount", new Document("$sum", 1)));
        Bson lookup = lookup("person", "_id", "_id", "speakerData");
        Bson unwind = unwind("$speakerData");
        Bson limit = limit(limiter);
        Bson sort = sort(descending("speechescount"));
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(group, lookup, unwind, limit));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "", "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter, "");
        }
        if (!partyFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", "", partyFilter);
        }

        ArrayList<JSONObject> objList = new ArrayList<>();
        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject obj = new JSONObject();
                    Document doc = (Document) procBlock.get("speakerData");
                    obj.put("speechCount", procBlock.getInteger("speechesCount"));
                    obj.put("speakerName", "" + doc.getString("title") + doc.getString("firstName") + " " + doc.getString("lastName"));
                    obj.put("picture", doc.get("picture"));
                    objList.add(obj);
                });
        return objList;
    }


    /**
     * returns count of all Tokens
     * @param limit
     * @author Edvin Nise
     */
    public ArrayList<JSONObject> getTokenCount(int limit,
                                               String dateFilterOne,
                                               String dateFilterTwo,
                                               String fractionFilter,
                                               String partyFilter,
                                               String personFilter) {
        Bson unwind = unwind("$tokens");
        Bson group = group("$tokens.lemmaValue", sum("count", 1));
        Bson sort = sort(descending("count"));
        Bson rankMode = limit(limit);

        List<Bson> pipeline = new ArrayList<>(Arrays.asList(unwind, group, sort, rankMode));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "", "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter, "");
        }
        if (!partyFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", "", partyFilter);
        }

        MongoIterable<Document> result = db.getCollection("speech_token")
                .aggregate(pipeline);
        ArrayList<JSONObject> objList = new ArrayList<>();
        for (Document doc : result) {
            JSONObject obj = new JSONObject();
            obj.put(doc.getString("_id"), doc.getInteger("count"));
            objList.add(obj);
        }
        return objList;
    }

    /**
     * returns all Named Entities with their respective count
     * @param dateFilterOne
     * @param dateFilterTwo
     * @param fractionFilter
     * @param partyFilter
     * @param personFilter
     * @author Edvin Nise
     */
    public JSONObject getNamedEntityCount(String dateFilterOne,
                                          String dateFilterTwo,
                                          String fractionFilter,
                                          String partyFilter,
                                          String personFilter) {

        //groups the documents by date and all array sizes for each named entity
        Bson group = group(new Document("_id", "$date"),
                sum("namedEntityPerson", new Document("$size", "$namedEntitiesPer")),
                sum("namedEntityLocation", new Document("$size", "$namedEntitiesLoc")),
                sum("namedEntityOrg", new Document("$size", "$namedEntitiesOrg")));

        Bson sort = sort(descending("namedEntityPerson"));
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(group, sort));

        //final JSON where all extracted data will end up
        JSONObject obj = new JSONObject();

        //adds date filter to the front of the pipeline
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        //adds fraction filter to the front of the pipeline
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "", "");
        }
        //adds person filter to the front of the pipeline
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter, "");
        }
        //adds party filter to the front of the pipeline
        if (!partyFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", "", partyFilter);
        }

        db.getCollection("speech").aggregate(pipeline).allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock ->
                {
                    //gets date from _id since the group pipeline stage creates nested JSON with date as id
                    Document doc = (Document) procBlock.get("_id");
                    //JSON for each named entity
                    JSONObject objEnt = new JSONObject();
                    objEnt.put("personEntity", procBlock.getInteger("namedEntityPerson"));
                    objEnt.put("locationEntity", procBlock.getInteger("namedEntityLocation"));
                    objEnt.put("orgEntity", procBlock.getInteger("namedEntityOrg"));
                    obj.put("" + (dateToLocalDate((Date) doc.get("_id"))), objEnt);
                });
        return obj;
    }

    /**
     * returns the count for all Parts of Speech
     * @param dateFilterOne
     * @param dateFilterTwo
     * @param fractionFilter
     * @param partyFilter
     * @param personFilter
     * @author Edvin Nise
     */
    @Unfinished("waiting for final structure of collection")
    public ArrayList<JSONObject> getPOSCount(String dateFilterOne,
                                             String dateFilterTwo,
                                             String fractionFilter,
                                             String partyFilter,
                                             String personFilter) {

        Bson unwind = unwind("$tokens");
        Bson project = project(new Document("OnlyPOS", "$tokens.coarsePOS"));
        Bson group = group(new Document("_id", "$OnlyPOS"), sum("CountOfPOS", 1));
        Bson sort = sort(descending("CountOfPOS"));
        ArrayList<JSONObject> objList = new ArrayList<>();

        List<Bson> pipeline = new ArrayList<>(Arrays.asList(unwind, project, group, sort));

        //adds date filter to the front of the pipeline
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        //adds fraction filter to the front of the pipeline
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "", "");
        }
        //adds person filter to the front of the pipeline
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter, "");
        }
        //adds party filter to the front of the pipeline
        if (!partyFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", "", partyFilter);
        }

        db.getCollection("speech_token").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject obj = new JSONObject();
                    Document doc = (Document) procBlock.get("_id");
                    obj.put(doc.getString("_id"), procBlock.getInteger("CountOfPOS"));
                    objList.add(obj);
                });
        return objList;
    }

    /**
     * return the percentage for each sentiment option
     *
     * @param dateFilterOne
     * @param dateFilterTwo
     * @param fractionFilter
     * @param personFilter
     * @param partyFilter
     * @return JSONObject
     * @author Edvin Nise
     */
    public JSONObject getSentimentData(String dateFilterOne,
                                       String dateFilterTwo,
                                       String fractionFilter,
                                       String personFilter,
                                       String partyFilter) {

        //match for comment sentiment filters since not all comments have a commentator
        Bson match = new Document("$match", new Document("$and", Arrays.asList(
                new Document("commentatorID", new Document("$ne", "N/A")),
                new Document("speakerID", new Document("$ne", "")))));

        Bson group = new Document("$group", new Document("_id", null)
                .append("count", new Document("$sum", 1))
                .append("pos", new Document("$sum", new Document("$cond",
                        Arrays.asList(new Document("$gt", Arrays.asList("$sentiment", 0)), 1, 0))))
                .append("neg", new Document("$sum", new Document("$cond",
                        Arrays.asList(new Document("$lt", Arrays.asList("$sentiment", 0)), 1, 0))))
                .append("neu", new Document("$sum", new Document("$cond",
                        Arrays.asList(new Document("$eq", Arrays.asList("$sentiment", 0)), 1, 0)))));

        //calculates the percentage of positive, negative and neutral sentiments over all comments and speeches
        Bson addFields = new Document("$addFields", new Document()
                .append("posPercent", new Document("$divide", Arrays.asList("$pos", "$count")))
                .append("negPercent", new Document("$divide", Arrays.asList("$neg", "$count")))
                .append("neuPercent", new Document("$divide", Arrays.asList("$neu", "$count"))));


        List<Bson> pipelineSpeech = new ArrayList<>(Arrays.asList(group, addFields));
        List<Bson> pipelineComment = new ArrayList<>(Arrays.asList(group, addFields));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipelineComment, dateFilterOne, dateFilterTwo);
            applyDateFiltersToAggregation(pipelineSpeech, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipelineComment, fractionFilter, "", "");
            applyPersonFractionFiltersToAggregation(pipelineSpeech, fractionFilter, "", "");
            pipelineComment.add(0, match);
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipelineComment, "", personFilter, "");
            applyPersonFractionFiltersToAggregation(pipelineSpeech, "", personFilter, "");
            pipelineComment.add(0, match);
        }
        if (!partyFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipelineComment, "", "", partyFilter);
            applyPersonFractionFiltersToAggregation(pipelineSpeech, "", "", partyFilter);
            pipelineComment.add(0, match);
        }
        JSONObject obj = new JSONObject();
        db.getCollection("speech").aggregate(pipelineSpeech).forEach((Consumer<? super Document>) procBlock ->
        {
            obj.put("speechPos", DECIMAL_FORMAT.format(procBlock.getDouble("posPercent") * 100));
            obj.put("speechNeg", DECIMAL_FORMAT.format(procBlock.getDouble("negPercent") * 100));
            obj.put("speechNeu", DECIMAL_FORMAT.format(procBlock.getDouble("neuPercent") * 100));
        });
        db.getCollection("comment").aggregate(pipelineComment).forEach((Consumer<? super Document>) procBlock -> {

            obj.put("commentPos", DECIMAL_FORMAT.format(procBlock.getDouble("posPercent") * 100));
            obj.put("commentNeg", DECIMAL_FORMAT.format(procBlock.getDouble("negPercent") * 100));
            obj.put("commentNeu", DECIMAL_FORMAT.format(procBlock.getDouble("neuPercent") * 100));
        });
        return obj;
    }

    /**
     * returns who commented on which speaker
     *
     * @param dateFilterOne
     * @param dateFilterTwo
     * @return
     * @author Edvin Nise
     */
    @Unfinished("waiting for correct structure of collection")
    public JSONObject commentatorToSpeaker(String dateFilterOne, String dateFilterTwo) throws ParseException {

        Bson match = new Document("$match", new Document("$and", Arrays.asList(
                new Document("commentatorID", new Document("$ne", "N/A")),
                new Document("speakerID", new Document("$ne", "")))));
        Bson lookupCommentator = lookup("person", "commentatorID", "_id", "CommentatorPerson");
        Bson lookupSpeaker = lookup("person", "speakerID", "_id", "SpeakerPerson");
        Bson unwindCommentator = unwind("$CommentatorPerson");
        Bson unwindSpeaker = unwind("$SpeakerPerson");
        Bson limit = limit(10000);


        List<Bson> pipeline = new ArrayList<>(Arrays.asList(limit, match, lookupCommentator, lookupSpeaker, unwindCommentator, unwindSpeaker));
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        //need to convert JSONObject to String, because duplicate strings are filtered by a HashSet while JSONObjects are not
        JSONObject obj = new JSONObject();
        HashSet<String> objNodesStrings = new HashSet<>();
        HashSet<String> objLinksStrings = new HashSet<>();
        ArrayList<org.json.simple.JSONObject> objNodes = new ArrayList<>();
        ArrayList<org.json.simple.JSONObject> objLinks = new ArrayList<>();
        db.getCollection("comment").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    Document docComment = (Document) procBlock.get("CommentatorPerson");
                    Document docSpeaker = (Document) procBlock.get("SpeakerPerson");
                    JSONObject objLink = new JSONObject();
                    objLink.put("source", docComment.getString("fullName"));
                    objLink.put("target", docSpeaker.getString("fullName"));
                    objLink.put("sentiment", procBlock.getDouble("sentiment"));
                    objLinksStrings.add(objLink.toString());

                    JSONObject objCommentator = new JSONObject();
                    objCommentator.put("name", docComment.getString("fullName"));
                    switch (docComment.getString("party")) {
                        case "CDU":
                        case "CSU":
                            objCommentator.put("group", 1);
                            break;
                        case "SPD":
                            objCommentator.put("group", 2);
                            break;
                        case "FDP":
                            objCommentator.put("group", 3);
                            break;
                        case "BÜNDNIS 90/DIE GRÜNEN":
                            objCommentator.put("group", 4);
                            break;
                        case "DIE LINKE.":
                            objCommentator.put("group", 5);
                            break;
                        case "AfD":
                            objCommentator.put("group", 6);
                            break;
                        default:
                            objCommentator.put("group", 7);
                    }
                    objNodesStrings.add(objCommentator.toString());

                    JSONObject objSpeaker = new JSONObject();
                    objSpeaker.put("name", docSpeaker.getString("fullName"));
                    switch (docSpeaker.getString("party")) {
                        case "CDU":
                        case "CSU":
                            objSpeaker.put("group", 1);
                            break;
                        case "SPD":
                            objSpeaker.put("group", 2);
                            break;
                        case "FDP":
                            objSpeaker.put("group", 3);
                            break;
                        case "BÜNDNIS 90/DIE GRÜNEN":
                            objSpeaker.put("group", 4);
                            break;
                        case "DIE LINKE.":
                            objSpeaker.put("group", 5);
                            break;
                        case "AfD":
                            objSpeaker.put("group", 6);
                            break;
                        default:
                            objSpeaker.put("group", 7);
                    }
                    objNodesStrings.add(objSpeaker.toString());
                });

        JSONParser parser = new JSONParser();
        for (String s : objLinksStrings) {
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(s);
            objLinks.add(json);
        }
        for (String s : objNodesStrings) {
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(s);
            objNodes.add(json);
        }
        obj.put("nodes", objNodes);
        obj.put("links", objLinks);
        return obj;
    }

    /**
     * returns speaker with corresponding topics
     * @param dateFilterTwo
     * @param dateFilterOne
     * @return JSONObject
     * @author Edvin Nise
     */
    @Testing
    public JSONObject matchSpeakerToDDC(String dateFilterOne, String dateFilterTwo) {

        Bson lookup = lookup("person", "speakerID", "_id", "speakerData");
        Bson unwind = unwind("$speakerData");

        Bson group = new Document("$group", new Document("_id", "$speakerData.fullName")
                .append("DDCKategorien", new Document("$push", "$mainTopic"))
                .append("fraction", new Document("$first", "$speakerData.party")));
        Bson limit = limit(5000);

        List<Bson> pipeline = new ArrayList<>(Arrays.asList(limit, lookup, unwind, group));
        JSONObject obj = new JSONObject();
        ArrayList<JSONObject> objNodes = new ArrayList<>();
        ArrayList<JSONObject> objLinks = new ArrayList<>();

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }

        HashSet<String> allDDCUnique = new HashSet<>();
        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock ->
                        {
                            JSONObject objName = new JSONObject();
                            objName.put("name", procBlock.getString("_id"));
                            switch (procBlock.getString("fraction")) {
                                case "CDU":
                                case "CSU":
                                    objName.put("group", 1);
                                    break;
                                case "SPD":
                                    objName.put("group", 2);
                                    break;
                                case "FDP":
                                    objName.put("group", 3);
                                    break;
                                case "BÜNDNIS 90/DIE GRÜNEN":
                                    objName.put("group", 4);
                                    break;
                                case "DIE LINKE.":
                                    objName.put("group", 5);
                                    break;
                                case "AfD":
                                    objName.put("group", 6);
                                    break;
                                default:
                                    objName.put("group", 7);
                            }
                            objNodes.add(objName);
                            //removes duplicates so that later on there won't be multiple similiar links
                            HashSet<String> uniqueDDCPerSpeech = new HashSet<>();
                            for (String ddc : (ArrayList<String>) procBlock.get("DDCKategorien")) {
                                uniqueDDCPerSpeech.add(ddc);
                                allDDCUnique.add(ddc);
                            }
                            for (String s : uniqueDDCPerSpeech) {
                                JSONObject objlink = new JSONObject();
                                objlink.put("source", procBlock.getString("_id"));
                                objlink.put("target", s);
                                objLinks.add(objlink);
                            }
                        }
                );

        for (String s : allDDCUnique) {
            JSONObject objNodeDDC = new JSONObject();
            objNodeDDC.put("name", s);
            objNodeDDC.put("group", 8);
            objNodes.add(objNodeDDC);
        }

        obj.put("nodes", objNodes);
        obj.put("links", objLinks);
        return obj;
    }

    public JSONObject speechSentTopicData(String dateFilterOne, String dateFilterTwo) throws ParseException {
        Bson limit = limit(1000);
        Bson lookup = lookup("person", "speakerID", "_id", "speakerData");
        Bson unwind = unwind("$speakerData");
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(limit, lookup, unwind));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }

        JSONObject obj = new JSONObject();
        HashSet<String> nodesUniqueSet = new HashSet<>();
        HashSet<String> uniqueLinks = new HashSet<>(); //perhaps unnnecessary
        ArrayList<org.json.simple.JSONObject> allNodesList = new ArrayList<>();
        ArrayList<org.json.simple.JSONObject> allLinksList = new ArrayList<>();
        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock ->
                        {
                            Document doc = (Document) procBlock.get("speakerData");
                            JSONObject objNode = new JSONObject();
                            objNode.put("name", procBlock.getString("_id"));
                            switch (doc.getString("party")) {
                                case "CDU":
                                case "CSU":
                                    objNode.put("group", 1);
                                    break;
                                case "SPD":
                                    objNode.put("group", 2);
                                    break;
                                case "FDP":
                                    objNode.put("group", 3);
                                    break;
                                case "BÜNDNIS 90/DIE GRÜNEN":
                                    objNode.put("group", 4);
                                    break;
                                case "DIE LINKE.":
                                    objNode.put("group", 5);
                                    break;
                                case "AfD":
                                    objNode.put("group", 6);
                                    break;
                                default:
                                    objNode.put("group", 7);
                            }
                            nodesUniqueSet.add(objNode.toString());
                            JSONObject objDDC = new JSONObject();
                            objDDC.put("name", procBlock.getString("mainTopic"));
                            objDDC.put("group", 8);
                            nodesUniqueSet.add(objDDC.toString());

                            JSONObject objLink = new JSONObject();
                            objLink.put("source", procBlock.getString("_id"));
                            objLink.put("target", procBlock.getString("mainTopic"));
                            objLink.put("sentiment", procBlock.getDouble("sentiment"));
                            uniqueLinks.add(objLink.toString());
                        }
                );
        JSONParser parser = new JSONParser();
        for (String s : nodesUniqueSet) {
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(s);
            allNodesList.add(json);
        }
        for (String s : uniqueLinks) {
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(s);
            allLinksList.add(json);
        }
        obj.put("nodes", allNodesList);
        obj.put("links", allLinksList);
        return obj;
    }

    /**
     * find all speeches that follow the search pattern*
     *
     * @param textFilter
     * @author Edvin Nise
     */
    @Unfinished("Need to know what data we will need for the visualisation")
    public JSONObject findSpeech(String textFilter) {
        Bson match = match(new Document("$text", new Document("$search", textFilter)));
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(match));

        JSONObject obj = new JSONObject();
        ArrayList<String> speechIDs = new ArrayList<>();

        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    speechIDs.add(procBlock.getString("_id"));
                });
        obj.put("speechIDs", speechIDs);
        return obj;
    }

    /**
     * returns all required Data for visualisation of a speech
     * @param redeID
     * @author Edvin Nise
     */
    public JSONObject allSpeechData(String redeID) {
        //first pipeline to get all speech data without comments
        Bson matchSpeech = match(new Document("_id", new Document("$eq", redeID)));
        Bson lookupSpeaker = lookup("person", "speakerID", "_id", "speaker");
        Bson unwindSpeaker = new Document("$unwind", new Document("path", "$speaker")
                .append("preserveNullAndEmptyArrays", true));
        List<Bson> pipelineSpeech = new ArrayList<>(Arrays.asList(matchSpeech, lookupSpeaker, unwindSpeaker));

        //second pipeline to get the comments from a speech with their commentator
        Bson matchSpeechID = match(new Document("speechID", new Document("$eq", redeID)));
        Bson lookupCommentator = lookup("person", "commentatorID", "_id", "commentatorData");
        Bson unwindCommentatorData = new Document("$unwind", new Document("path", "$commentatorData")
                .append("preserveNullAndEmptyArrays", true));
        List<Bson> pipelineComments = new ArrayList<>(Arrays.asList(matchSpeechID, lookupCommentator, unwindCommentatorData));

        JSONObject obj = new JSONObject();
        ArrayList<JSONObject> comments = new ArrayList<>();

        MongoIterable<Document> resultSpeech = db.getCollection("speech").aggregate(pipelineSpeech)
                .allowDiskUse(false);
        for (Document docSpeech : resultSpeech) {

            obj.put("speechID", docSpeech.getString("_id"));
            obj.put("speakerID", docSpeech.getString("speakerID"));
            obj.put("text", docSpeech.getString("text"));
            obj.put("speechSentiment", docSpeech.getDouble("sentiment"));
            obj.put("sentences", docSpeech.get("sentences"));
            obj.put("namedEntitiesPer", docSpeech.get("namedEntitiesPer"));
            obj.put("namedEntitiesLoc", docSpeech.get("namedEntitiesLoc"));
            obj.put("namedEntitiesOrg", docSpeech.get("namedEntitiesOrg"));
            obj.put("date", TimeHelper.mongoDateToPrettyGermanDate(docSpeech.getDate("date")));
            obj.put("speaker", docSpeech.get("speaker"));
        }
        MongoIterable<Document> resultComments = db.getCollection("comment").aggregate(pipelineComments)
                .allowDiskUse(false);
        for (Document docComment : resultComments) {

            JSONObject objComment = new JSONObject();
            objComment.put("id", docComment.getString("_id"));
            objComment.put("speakerID", docComment.getString("speakerID"));
            objComment.put("commentPos", docComment.getInteger("commentPos"));
            objComment.put("commentText", docComment.getString("text"));
            objComment.put("sentiment", docComment.getDouble("sentiment"));
            objComment.put("commentator", docComment.get("commentatorData"));
            comments.add(objComment);
        }
        obj.put("commentData", comments);
        return obj;
    }

    /**
     * returns all poll results from all named fractions
     * @param dateFilterOne
     * @param dateFilterTwo
     * @param personFilter
     * @param partyFilter
     * @param fractionFilter
     * @author Edvin Nise
     */
    @Unfinished("Dont know where we save this data")
    public ArrayList<JSONObject> getPollResults(String dateFilterOne,
                                                String dateFilterTwo,
                                                String fractionFilter,
                                                String partyFilter,
                                                String personFilter) {

        //calculates total votes for each party and also for each type of vote
        Bson addFieldsVotesData = new Document("$addFields", new Document()
                .append("totalVotesSPD", new Document("$add", Arrays.asList("$SPDYes", "$SPDNo", "$SPDAbstained", "$SPDNoVotes")))
                .append("totalVotesAfD", new Document("$add", Arrays.asList("$AfDYes", "$AfDNo", "$AfDAbstained", "$AfDNoVotes")))
                .append("totalVotesCxU", new Document("$add", Arrays.asList("$CxUYes", "$CxUNo", "$CxUAbstained", "$CxUNoVotes")))
                .append("totalVotesB90", new Document("$add", Arrays.asList("$B90Yes", "$B90No", "$B90Abstained", "$B90NoVotes")))
                .append("totalVotesFDP", new Document("$add", Arrays.asList("$FDPYes", "$FDPNo", "$FDPAbstained", "$FDPNoVotes")))
                .append("totalVotesLINKE", new Document("$add", Arrays.asList("$LINKEYes", "$LINKENo", "$LINKEAbstained", "$LINKENoVotes")))
                .append("totalVotesindependent", new Document("$add", Arrays.asList("$independentYes",
                        "$independentNo", "$independentAbstained", "$independentNoVotes")))
                .append("totalVotesYes", new Document("$add", Arrays.asList("$SPDYes", "$AfDYes", "$CxUYes", "$B90Yes",
                        "$FDPYes", "$LINKEYes", "$independentYes")))
                .append("totalVotesNo", new Document("$add", Arrays.asList("$SPDNo", "$AfDNo", "$CxUNo", "$B90No",
                        "$FDPNo", "$LINKENo", "$independentNo")))
                .append("totalVotesAbstained", new Document("$add", Arrays.asList("$SPDAbstained", "$AfDAbstained",
                        "$CxUAbstained", "$B90Abstained", "$FDPAbstained", "$LINKEAbstained", "$independentAbstained")))
                .append("totalVotesNoVotes", new Document("$add", Arrays.asList("$SPDNoVotes", "$AfDNoVotes",
                        "$CxUNoVotes", "$B90NoVotes", "$FDPNoVotes", "$LINKENoVotes", "$independentNoVotes"))));

        //cannot use newly created field for another field addition so i had to add a pipeline stage
        Bson addFieldsTotalVotes = new Document("$addFields", new Document()
                .append("totalVotes", new Document("$add", Arrays.asList("$totalVotesYes", "$totalVotesNo",
                        "$totalVotesAbstained", "$totalVotesNoVotes"))));


        List<Bson> pipeline = new ArrayList<>(Arrays.asList(addFieldsVotesData, addFieldsTotalVotes));
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "", "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter, "");
        }
        if (!partyFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", "", partyFilter);
        }
        ArrayList<JSONObject> objList = new ArrayList<>();
        String[] partyList = new String[]{"SPD", "AfD", "CxU", "B90", "FDP", "LINKE", "independent"};
        db.getCollection("poll").aggregate(pipeline).allowDiskUse(false).forEach((Consumer<? super Document>) procBlock -> {
            JSONObject objTotal = new JSONObject();
            objTotal.put("totalVotes", procBlock.getInteger("totalVotes"));
            objTotal.put("totalYes", procBlock.getInteger("totalVotesYes"));
            objTotal.put("totalNo", procBlock.getInteger("totalVotesNo"));
            objTotal.put("totalAbstained", procBlock.getInteger("totalVotesAbstained"));
            objTotal.put("totalNoVotes", procBlock.getInteger("totalVotesNoVotes"));
            objTotal.put("date", (dateToLocalDate(procBlock.getDate("date"))));
            objTotal.put("topic", procBlock.getString("topic"));
            objTotal.put("pollID", procBlock.getString("_id"));
            //get polldata for all parties
            for (String s : partyList) {
                if (!procBlock.getInteger("totalVotes" + s).equals(0)) {

                    objTotal.put(s + "totalVotes", procBlock.getInteger("totalVotes" + s));
                    objTotal.put(s + "Yes", procBlock.getInteger(s + "Yes"));
                    objTotal.put(s + "No", procBlock.getInteger(s + "No"));
                    objTotal.put(s + "Abstained", procBlock.getInteger(s + "Abstained"));
                    objTotal.put(s + "NoVotes", procBlock.getInteger(s + "NoVotes"));
                }
            }

            objList.add(objTotal);
        });
        return objList;
    }

    /**
     * returns JSON Object for traversing through agendaitems to find speeches bound to them
     * @author Edvin Nise
     */
    public JSONObject getProtocolAgendaPersonData() {
        JSONObject obj = new JSONObject();

        JSONObject protocols = new JSONObject();
        JSONObject agendaItems = new JSONObject();
        JSONObject people = new JSONObject();
        //Finds all protocols and their agenda items
        db.getCollection("protocol").find()
                .forEach((Consumer<? super Document>) procBlock -> protocols.put(procBlock.getString("_id"),
                        (ArrayList<String>) procBlock.get("agendaItems")));
        //Finds all agendaitems, their subject and the related speeches
        db.getCollection("agendaItem").find()
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject agendaItem = new JSONObject();
                    agendaItem.put("speechIDs",
                            (ArrayList<String>) procBlock.get("speechIDs"));
                    agendaItem.put("subject", procBlock.get("subject"));
                    agendaItems.put(procBlock.getString("_id"), agendaItem);
                });
        //Finds all people, their full names and their party
        db.getCollection("person").find()
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject person = new JSONObject();
                    person.put("fullName", procBlock.getString("fullName"));
                    person.put("party", procBlock.getString("party"));
                    people.put(procBlock.getString("_id"), person);
                });
        obj.put("protocols", protocols);
        obj.put("agendaItems", agendaItems);
        obj.put("people", people);
        return obj;
    }

    /**
     * create a text index for a collection by indexing a specific field
     * @param col
     * @param field
     * @author Edvin Nise
     */
    public void createTextIndex(String col, String field) {
        db.getCollection(col).createIndex(Indexes.text(field));
        System.out.println("Created text index " + field + "_text successfully!");
    }

    /**
     * drops the text index of a given collection
     * @param col
     * @param field
     * @author Edvin Nise
     */
    public void dropTextIndex(String col, String field) {
        db.getCollection(col).dropIndex(field + "_text");
    }


    /**
     * This method creates a unique cookie from the username and password
     *
     * @param message
     * @return
     * @author Julian Ocker
     */
    public String hashDataIntoCookie(String message) {
        String finalMessage;
        try {
            byte[] byteMessage = message.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            MessageDigest messageDigest1 = MessageDigest.getInstance("SHA-1");
            MessageDigest messageDigest2 = MessageDigest.getInstance("SHA-512");
            MessageDigest messageDigest3 = MessageDigest.getInstance("MD5");
            messageDigest3.update(byteMessage);
            byte[] md5Message = messageDigest3.digest();
            messageDigest1.update(byteMessage);
            byte[] sha1Message = messageDigest1.digest();
            byte[] concat1Message = (DatatypeConverter.printHexBinary(sha1Message).toLowerCase() + Math.random() +
                    LocalDateTime.now() + DatatypeConverter.printHexBinary(md5Message).toLowerCase()).getBytes();
            byte[] concat2Message = (DatatypeConverter.printHexBinary(md5Message).toLowerCase() + Math.random() +
                    LocalDateTime.now() + DatatypeConverter.printHexBinary(sha1Message).toLowerCase()).getBytes();
            messageDigest.update(concat1Message);
            messageDigest2.update(concat2Message);
            byte[] result = (DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase() + Math.random()
                    + DatatypeConverter.printHexBinary(messageDigest2.digest()).toLowerCase()).getBytes();
            finalMessage = DatatypeConverter.printHexBinary(result).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return finalMessage;
    }

    /**
     * This method checks whether the login-data are valid and if that is the case request and returns a cookie;
     *
     * @param name
     * @param password
     * @return
     * @author Julian Ocker
     */
    public String generateCookie(String name, String password) {
        String cookie = "";
        if (checkUserAndPassword(name, password)) {
            String rank = getTag("user", "_id", name, "rank");
            cookie = hashDataIntoCookie(Math.random() + name + password + rank + LocalDateTime.now() + Math.random());
            db.getCollection("cookies").insertOne(
                    new Document()
                            .append("user", name)
                            .append("_id", cookie)
                            .append("rank", rank)
                            .append("expiring", LocalTime.now())
            );
        }
        return cookie;
    }

    /**
     * This method checks whether a username is available
     *
     * @param name
     * @return
     * @author Julian Ocker
     */
    public boolean checkIfAvailable(String name) {
        if (db.getCollection("user").find(new Document("_id", name)).iterator().hasNext()) {
            return false;
        }
        return true;
    }

    /**
     * This method registers a new User if the Username is available.
     *
     * @param name
     * @param password
     * @param rank
     * @return
     * @author Julian Ocker
     */
    public Boolean register(String name, String password, String rank) {
        if (password.equals("da39a3ee5e6b4b0d3255bfef95601890afd80709")) {
            return false;
        }
        try {
            String salt = generateSalt();
            db.getCollection("user").insertOne(
                    new Document("_id", name)
                            .append("rank", rank)
                            .append("password", hashPasswordWithSalt(password, salt))
                            .append("salt", salt)
            );
            return true;
        } catch (Exception ignored) {
            return false;
        }

    }

    /**
     * This method gets the Rank of an User by Cookie.
     *
     * @param cookie
     * @param feature
     * @return
     * @author Julian Ocker
     */
    public Boolean checkIfCookieIsAllowedAFeature(String cookie, String feature) {
        String rank = getRankOfCookie(cookie);
        String featureRank = getRankOfFeature(feature);
        switch (featureRank) {
            case "everyone":
                return true;
            case "user":
                return !rank.equals("everyone");
            case "manager":
                return !rank.equals("everyone") && !rank.equals("user");
            case "admin":
                return rank.equals("admin");
            default:
                return false;
        }
    }

    /**
     * This method gets the Rank of an User by Cookies.
     *
     * @param feature
     * @return
     * @author Julian Ocker
     */
    public String getRankOfFeature(String feature) {
        return getTag("features", "_id", feature, "rank");
    }

    /**
     * This function creates a User of the rank admin.
     * Username: Admin1
     * password: admin
     *
     * @author Julian Ocker
     */
    public void createUserCollection() {
        String salt = generateSalt();
        this.db.getCollection("user").insertOne(new Document()
                .append("_id", "Admin1")
                .append("rank", "admin")
                .append("password", hashPasswordWithSalt("d033e22ae348aeb5660fc2140aec35850c4da997", salt))
                .append("salt", salt)
        );
    }

    /**
     * This function checks if the password is correct and changes it to  new password if thet is the case.
     *
     * @param cookie
     * @param newPassword
     * @param oldPassword
     * @return
     * @author Julian Ocker
     */
    public boolean changePassword(String cookie, String newPassword, String oldPassword) {
        if (oldPassword.equals("da39a3ee5e6b4b0d3255bfef95601890afd80709")) {
            return false;
        }
        String username = getTag("cookies", "_id", cookie, "user");
        String rank = getTag("cookies", "_id", cookie, "rank");
        if (checkUserAndPassword(username, oldPassword)) {
            db.getCollection("user").deleteOne(
                    new Document("_id", username)
            );
            db.getCollection("user").insertOne(
                    new Document("_id", username)
                            .append("rank", rank)
                            .append("password", newPassword)
            );
            return true;
        } else {
            return false;
        }

    }

    /**
     * This Method returns a specific field from a document.
     *
     * @param collection
     * @param column
     * @param id
     * @param tag
     * @return
     * @throws NullPointerException
     * @author JulianOcker
     */
    public String getTag(String collection, String column, String id, String tag) throws NullPointerException {
        Document result = db.getCollection(collection).find(new Document(column, id)).iterator().tryNext();
        if (result == null)
            throw new NullPointerException("The Document with _id = " + id + " does not exist in this collection.");
        return result.getString(tag);
    }

    /**
     * This method checks whether a user exists and if the password is correct.
     *
     * @param id
     * @param password
     * @return
     * @author Julian Ocker
     */
    public boolean checkUserAndPassword(String id, String password) {
        if (db.getCollection("user").find(new Document("_id", id)).iterator().hasNext()) {
            Document userInQuestion = db.getCollection("user").find(new Document("_id", id)).iterator().next();
            if (hashPasswordWithSalt(password, userInQuestion.getString("salt")).equals(
                    userInQuestion.getString("password"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method deletes a cookie.
     *
     * @param name
     * @return
     * @author Julian Ocker
     */
    public boolean logout(String name) {
        if (db.getCollection("cookies").find(new Document("_id", name)).iterator().hasNext()) {
            db.getCollection("cookies").deleteOne(new Document("_id", name));
            return true;
        }
        return false;
    }

    /**
     * This method delestes a User by Username.
     *
     * @param name
     * @return
     * @author Julian Ocker
     */
    public Boolean deleteUser(String name) {
        if (!db.getCollection("user").find(new Document("_id", name)).iterator().hasNext()) {
            return false;
        }
        try {
            db.getCollection("user").deleteOne(new Document("_id", name));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * This method deletes a user from the db and creates a new one with partly changed attributes
     *
     * @param oldID
     * @param newID
     * @param newPassword
     * @param newRank
     * @return
     * @author Julian Ocker
     */
    public Boolean editUser(String oldID, String newID, String newPassword, String newRank) throws NoSuchAlgorithmException {
        if (newPassword.equals("da39a3ee5e6b4b0d3255bfef95601890afd80709")) {
            newPassword = "";
        }
        if (db.getCollection("user").find(new Document("_id", oldID)).iterator().hasNext()
                && !checkIfDocumentExists("User", newID)) {
            Document editUser = db.getCollection("user").find(new Document("_id", oldID)).iterator().next();
            db.getCollection("user").deleteOne(new Document("_id", oldID));
            if (newRank.equals("user") || newRank.equals("manager") || newRank.equals("admin")) {
            } else {
                newRank = "";
            }
            if (!newID.isEmpty()) {
                editUser.put("_id", newID);
            }
            if (!newPassword.isEmpty()) {
                editUser.put("password", hashPasswordWithSalt(newPassword, editUser.getString("salt")));
            }
            if (!newRank.isEmpty()) {
                editUser.put("rank", newRank);
            }
            db.getCollection("user").insertOne(editUser);
            return true;
        } else {
            return false;
        }
    }

    /**
     * creates a random Salt for any password
     *
     * @return
     * @author Julian Ocker
     */
    public String generateSalt() {
        String salt = String.valueOf(Math.random());
        return salt;
    }

    /**
     * Hashes again with a salt
     *
     * @param password
     * @param salt
     * @return
     * @author Julian Ocker
     */
    public String hashPasswordWithSalt(String password, String salt) {
        String saltedPassword = "";
        try {
            String message = password + salt;
            byte[] byteMessage = message.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(byteMessage);
            saltedPassword = (DatatypeConverter.printHexBinary(byteMessage)).toString();
        } catch (Exception ignored) {
        }
        return saltedPassword;
    }

    /**
     * This function creates the Collection that contains the features and who is allowed to use them.
     *
     * @author Julian Ocker
     */
    public void createFeatureCollection() {
        db.getCollection("features").drop();
        db.getCollection("features").insertOne(new Document("_id", "editFeatures").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "editSpeeches").append("rank", "user"));
        db.getCollection("features").insertOne(new Document("_id", "editProtocols").append("rank", "user"));
        db.getCollection("features").insertOne(new Document("_id", "editAgendaItems").append("rank", "user"));
        db.getCollection("features").insertOne(new Document("_id", "editPersons").append("rank", "manager"));
        db.getCollection("features").insertOne(new Document("_id", "addSpeeches").append("rank", "manager"));
        db.getCollection("features").insertOne(new Document("_id", "addProtocols").append("rank", "manager"));
        db.getCollection("features").insertOne(new Document("_id", "addAgendaItems").append("rank", "manager"));
        db.getCollection("features").insertOne(new Document("_id", "addPersons").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "deleteSpeeches").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "deleteProtocols").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "deleteAgendaItems").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "deletePersons").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "editUsers").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "addUsers").append("rank", "admin"));
        db.getCollection("features").insertOne(new Document("_id", "deleteUsers").append("rank", "admin"));
    }

    /**
     * This method Checks whether a person is logged in.
     *
     * @param cookie
     * @return
     * @author Julian Ocker
     */
    public boolean checkIfCookieExists(String cookie) {
        return db.getCollection("cookies").find(new Document("_id", cookie)).iterator().hasNext();
    }

    /**
     * This method returns the rank of the User.
     *
     * @param cookie
     * @return
     * @author Julian Ocker
     */
    public String getRankOfCookie(String cookie) {
        String rank = "";
        if (cookie == null || cookie.equals("")) {
            rank = "everyone";
        } else {
            rank = getTag("cookies", "_id", cookie, "rank");
        }
        return rank;
    }

    /**
     * This allows the User to edit the Features to a certain degree.
     *
     * @param featureToEdit
     * @param editRank
     * @return
     * @author Julian Ocker
     */
    public boolean editFeature(String featureToEdit, String editRank) {
        if (!(editRank.equals("everyone") || editRank.equals("user") || editRank.equals("manager") || editRank.equals("admin") || editRank.equals("nobody"))) {
            return false;
        }
        if (featureToEdit.equals("editFeatures") && editRank.equals("nobody")){
            editRank = "admin";
        }
        if (checkIfDocumentExists("features", featureToEdit)) {
            db.getCollection("features").deleteOne(new Document("_id", featureToEdit));
            db.getCollection("features").insertOne(new Document("_id", featureToEdit).append("rank", editRank));
            return true;
        } else {
            return false;
        }
    }
}
