package utility;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import data.*;
import data.impl.Person_Impl;
import exceptions.WrongInputException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import utility.annotations.*;
import utility.uima.ProcessedSpeech;

import javax.json.Json;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
@Unfinished("This class is unfinished")
public class MongoDBHandler {
    private final MongoDatabase db;
    private final InsertManyOptions imo = new InsertManyOptions().ordered(false);
    private final Gson gson = new Gson();

    /**
     * Connects to the MongoDB specified in {@code PRG_WiSe22_Group_9_4.txt}.
     *
     * @throws IOException if an error occurred while handling the properties file.
     * @author Eric Lakhter
     */
    public MongoDBHandler() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(MongoDBHandler.class.getClassLoader().getResource("PRG_WiSe22_Group_9_4.txt").getPath()));
        MongoClient client = MongoClients.create(
                "mongodb://" + prop.getProperty("remote_user") +
                        ":" + prop.getProperty("remote_password") +
                        "@" + prop.getProperty("remote_host") +
                        ":" + prop.getProperty("remote_port") +
                        "/?authSource=" + prop.getProperty("remote_user"));
        db = client.getDatabase(prop.getProperty("remote_database"));
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
            System.out.println(" Document  with _id:" + " id was not found in collection: " + col);
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
        } catch (MongoWriteException | IllegalArgumentException ignored) {
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
        } catch (MongoWriteException e) {
            System.err.println("Insert of person failed.");
        }
    }

    /**
     * Method to insert a single Protocol into the database in serialised form.
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
        } catch (MongoWriteException e) {
            System.err.println("Insert of protocol failed.");
        }
    }

    /**
     * Method to insert a list of Protocol Object into the database in serilised form.
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
        } catch (MongoWriteException e) {
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
        } catch (MongoWriteException | IllegalArgumentException e) {
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
        } catch (MongoWriteException | IllegalArgumentException ignored) {
        }

        //Insert single document into speech_cas collection
        try {
            db.getCollection("speech_cas").insertOne(Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()));
        } catch (MongoWriteException | IllegalArgumentException ignored) {
        }

        //Insert single document into speech_tokens collection
        try {
            db.getCollection("speech_tokens").insertOne(Document.parse(processedSpeech.toSpeechJson()).append("date", processedSpeech.getDate()));
        } catch (MongoWriteException | IllegalArgumentException ignored) {
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
            speechCasDocs.add(Document.parse(processedSpeech.toSpeechCasJson()).append("date", processedSpeech.getDate()));

            //parse into Bson Document for speech_tokens collection and add to list
            speechTokenDocs.add(Document.parse(processedSpeech.toSpeechTokensJson()).append("date", processedSpeech.getDate()));
        }

        // MongoBulkWriteExceptions are caught when inserting the Lists
        try {
            db.getCollection("speech").insertMany(speechDocs, imo);
        } catch (MongoBulkWriteException | IllegalArgumentException ignored) {
        }
        try {
            db.getCollection("speech_cas").insertMany(speechCasDocs, imo);
        } catch (MongoBulkWriteException | IllegalArgumentException ignored) {
        }
        try {
            db.getCollection("speech_token").insertMany(speechTokenDocs, imo);
        } catch (MongoBulkWriteException | IllegalArgumentException ignored) {
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
    public void insertAgendaItems(List<AgendaItem> agendaItems) throws WrongInputException {
        if (agendaItems == null || agendaItems.isEmpty()) {
            throw new WrongInputException("Input is null or empty");
        }
        ArrayList<Document> mongoAgendaItems = new ArrayList<>(0);
        for (AgendaItem agendaItem : agendaItems) {
            if (agendaItem == null) continue;
            mongoAgendaItems.add(new Document("_id", agendaItem.getDate())
                    .append("date", agendaItem.getDate())
                    .append("subject", agendaItem.getSubject())
                    .append("speechIDs", agendaItem.getSpeechIDs()));
        }
        try {
            db.getCollection("agendaItem").insertMany(mongoAgendaItems);
        } catch (MongoWriteException | IllegalArgumentException ignored) {
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
        } catch (MongoWriteException | IllegalArgumentException e) {
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
            pollDocs.add(Document.parse(poll.toJson()).append("date", poll.getDate()));
        }
        try {
            db.getCollection("poll").insertMany(pollDocs, imo);
        } catch (MongoBulkWriteException | IllegalArgumentException ignored) {
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
            } catch (MongoWriteException e) {
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
    public void addDocuments(List<Document> documents, String collection) {
        if (collectionExists(collection) && documents != null) {
            try {
                db.getCollection(collection).insertMany(documents);
            } catch (MongoWriteException e) {
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
    @Unfinished("Probably, needs to be adapted to the fraction 19, fraction 20 options." +
            " Possibly also needs to be changed to fit the needs of the visualisation in the front end.")
    public void applyPersonFractionFiltersToAggregation(List<Bson> pipeline, String fractionFilter, String personFilter, String... neededField) {//String personFilter : person Filter  parameter i temporarily took out
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
            pipeline.add(0, match(new Document("persondata.party", fractionFilter)));
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
    @Unfinished("Requires thorough testing still.")
    public MongoIterable<Document> runAggregationQueryWithFilters(
            String collection,
            String dateFilterOne,
            String dateFilterTwo,
            String personFilter,
            String fractionFilter,
            String sentimentFilter) {

        //create the pipeline
        List<Bson> pipeline = new ArrayList<>(0);

        //if datefilters are present add them to the pipeline
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        // if person or fraction filters are present, add them as well
        if (!personFilter.isEmpty() || !fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, personFilter, fractionFilter);
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
     *
     * @param dateFilterOne
     * @param dateFilterTwo
     * @author Edvin Nise
     */
    public ArrayList<JSONObject> getSpeechesBySpeakerCount(String dateFilterOne, String dateFilterTwo, String fractionFilter, String personFilter) {
        Bson group = new Document("$group", new Document("_id", "$speakerID").append("speechesCount", new Document("$sum", 1)));
        Bson lookup = lookup("person", "_id", "_id", "speakerData");
        Bson unwind = unwind("$speakerData");
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(group, lookup, unwind));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }

        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter);
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
        System.out.println(objList);
        return objList;
    }


    /**
     * returns count of all Tokens
     *
     * @param limit
     * @author Edvin Nise
     */
    public ArrayList<JSONObject> getTokenCount(int limit, String dateFilterOne, String dateFilterTwo, String fractionFilter, String personFilter) {
        Bson unwind = unwind("$tokens");
        Bson group = group("$tokens.lemmaValue", sum("count", 1));
        Bson sort = sort(descending("count"));
        Bson rankMode = limit(limit);
//        Bson rankMode = match(gte("count", limit));
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(unwind, group, sort, rankMode));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter);
        }

        MongoIterable<Document> result = db.getCollection("test_speech_token_edvin")
                .aggregate(pipeline);
        ArrayList<JSONObject> objList = new ArrayList<>();
        for (Document doc : result) {
            JSONObject obj = new JSONObject();
            obj.put(doc.getString("_id"), doc.getInteger("count"));
            objList.add(obj);
        }
        System.out.println(objList);
        return objList;
    }

    /**
     * returns all Named Entities with their respective count
     * @author Edvin Nise
     */
    public JSONObject getNamedEntityCount(String dateFilterOne, String dateFilterTwo, String fractionFilter, String personFilter) {
        Bson group = group(new Document("_id", "$date"),
                sum("namedEntityPerson", new Document("$size", "$namedEntitiesPer")),
                sum("namedEntityLocation", new Document("$size", "$namedEntitiesLoc")),
                sum("namedEntityOrg", new Document("$size", "$namedEntitiesOrg")));
        Bson sort = sort(descending("namedEntityPerson"));
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(group, sort));
        JSONObject obj = new JSONObject();

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter);
        }

        db.getCollection("speech").aggregate(pipeline).allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock ->
                {
                    Document doc = (Document) procBlock.get("_id");
                    JSONObject objEnt = new JSONObject();
                    objEnt.put("personEntity", procBlock.getInteger("namedEntityPerson"));
                    objEnt.put("locationEntity", procBlock.getInteger("namedEntityLocation"));
                    objEnt.put("orgEntity", procBlock.getInteger("namedEntityOrg"));
                    obj.put("" + (dateToLocalDate((Date) doc.get("_id"))), objEnt);
                });
       // System.out.println(obj);
        return obj;
    }

    /**
     * returns the count for all Parts of Speech
     *
     * @author Edvin Nise
     */
    @Unfinished("waiting for final structure of collection")
    public ArrayList<JSONObject> getPOSCount(String dateFilterOne, String dateFilterTwo,
                                             String fractionFilter, String personFilter) {
        Bson unwind = unwind("$tokens");
        Bson project = project(new Document("OnlyPOS", "$tokens.coarsePOS"));
        Bson group = group(new Document("_id", "$OnlyPOS"), sum("CountOfPOS", 1));
        Bson sort = sort(descending("CountOfPOS"));
        ArrayList<JSONObject> objList = new ArrayList<>();
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(unwind, project, group, sort));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter);
        }

        db.getCollection("speech_token").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject obj = new JSONObject();
                    Document doc = (Document) procBlock.get("_id");
                    obj.put(doc.getString("_id"), procBlock.getInteger("CountOfPOS"));
                    objList.add(obj);
                });
        System.out.println(objList);
        return objList;
    }

    public JSONObject getSentimentData(String dateFilterOne, String dateFilterTwo,
                                       String fractionFilter, String personFilter) {
        //Create two pipelines to get the total amount of positive, negative and neutral sentiments for both speeches and comments
        Bson facet = new Document("$facet", new Document()
                .append("speechSentimentPipeline", Arrays.asList(
                        new Document("$group", new Document("_id", null)
                                .append("count", new Document("$sum", 1))
                                .append("pos", new Document("$sum", new Document("$cond",
                                        Arrays.asList(new Document("$gt", Arrays.asList("$sentiment", 0)), 1, 0))))
                                .append("neg", new Document("$sum", new Document("$cond",
                                        Arrays.asList(new Document("$lt", Arrays.asList("$sentiment", 0)), 1, 0))))
                                .append("neu", new Document("$sum", new Document("$cond",
                                        Arrays.asList(new Document("$eq", Arrays.asList("$sentiment", 0)), 1, 0))))
                        )))
                .append("commentSentimentPipeline", Arrays.asList(
                        new Document("$lookup", new Document("from", "comment")
                                .append("localField", "_id")
                                .append("foreignField", "speechID")
                                .append("as", "comments")),
                        new Document("$unwind", "$comments"),
                        new Document("$group", new Document("_id", null)
                                .append("count", new Document("$sum", 1))
                                .append("pos", new Document("$sum", new Document("$cond",
                                        Arrays.asList(new Document("$gt", Arrays.asList("$comments.sentiment", 0)), 1, 0))))
                                .append("neg", new Document("$sum", new Document("$cond",
                                        Arrays.asList(new Document("$lt", Arrays.asList("$comments.sentiment", 0)), 1, 0))))
                                .append("neu", new Document("$sum", new Document("$cond",
                                        Arrays.asList(new Document("$eq", Arrays.asList("$comments.sentiment", 0)), 1, 0)))))
                ))
        );
        Bson unwindSpeech = unwind("$speechSentimentPipeline");
        Bson unwindComments = unwind("$commentSentimentPipeline");

        //adds Fields for the summed amount of all sentiment counts
        Bson addFields = new Document("$addFields", new Document("allCount", new Document("$add",
                Arrays.asList("$speechSentimentPipeline.count", "$commentSentimentPipeline.count")))
                .append("posCount", new Document("$add",
                        Arrays.asList("$speechSentimentPipeline.pos", "$commentSentimentPipeline.pos")))
                .append("negCount", new Document("$add",
                        Arrays.asList("$speechSentimentPipeline.neg", "$commentSentimentPipeline.neg")))
                .append("neuCount", new Document("$add",
                        Arrays.asList("$speechSentimentPipeline.neu", "$commentSentimentPipeline.neu"))));

        //calculates the percentage of positive, negative and neutral sentiments over all comments and speeches
        Bson project = project(new Document("posPercent", new Document("$divide", Arrays.asList("$posCount", "$allCount")))
                .append("negPercent", new Document("$divide", Arrays.asList("$negCount", "$allCount")))
                .append("neuPercent", new Document("$divide", Arrays.asList("$neuCount", "$allCount"))));


        List<Bson> pipeline = new ArrayList<>(Arrays.asList(facet, unwindSpeech, unwindComments, addFields, project));
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter);
        }
        JSONObject obj = new JSONObject();
        db.getCollection("speech").aggregate(pipeline).forEach((Consumer<? super Document>) procBlock -> {
            obj.put("positive", procBlock.getDouble("posPercent") * 100);
            obj.put("negative", procBlock.getDouble("negPercent") * 100);
            obj.put("neutral", procBlock.getDouble("neuPercent") * 100);
        });
        System.out.println(obj);
        return obj;
    }

    /**
     * returns who commented on which speaker
     *
     * @author Edvin Nise
     */
    @Unfinished("waiting for correct structure of collection")
    public ArrayList<Object> commentatorToSpeaker() {
        ArrayList<Object> commentatorToSpeakerData = new ArrayList<>();

        Bson lookupCommentator = lookup("person", "commentatorID", "_id", "CommentatorPerson");
        Bson lookupSpeaker = lookup("person", "speakerID", "_id", "SpeakerPerson");
        Bson unwindCommentator = unwind("$CommentatorPerson");
        Bson unwindSpeaker = unwind("$SpeakerPerson");
        Bson project = project(new Document("CommentatorName", new Document("$concat",
                Arrays.asList("$CommentatorPerson.firstName", " ", "$CommentatorPerson.lastName")))
                .append("SpeakerName", new Document("$concat", Arrays.asList("$SpeakerPerson.firstName", " ", "$SpeakerPerson.lastName")))
                .append("sentiment", 1));

        List<Bson> pipeline = new ArrayList<>(Arrays.asList(lookupCommentator, lookupSpeaker, unwindCommentator, unwindSpeaker, project));
//        if (!sent.isEmpty()) {
//            applySentimentFilterToAggregation(pipeline, sent);
//        }
        db.getCollection("comment").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject obj = new JSONObject();
                    obj.put("commentatorName", procBlock.getString("CommentatorName"));
                    obj.put("speakerName", procBlock.getString("SpeakerName"));
                    obj.put("sentiment", procBlock.getDouble("sentiment"));
                    commentatorToSpeakerData.add(obj);
                });
        System.out.println(commentatorToSpeakerData);
        return commentatorToSpeakerData;
    }

    /**
     * returns speaker with corresponding topics
     *
     * @author Edvin Nise
     */
    @Unfinished("Waiting for structure of collection")
    public JSONObject matchSpeakerToDDC() {
        JSONObject speakerWithDDCJSON = new JSONObject();

        Bson lookup = lookup("person", "speakerID", "_id", "speakerData");
        Bson unwind = unwind("$speakerData");
        Bson project = project(new Document("Abgeordneter", new Document("$concat", Arrays.asList("$speakerData.firstName", " ", "$speakerData.lastName")))
                .append("mainTopic", 1)
                .append("maybeTopic", 1));
        Bson group = new Document("$group", new Document("_id", "$Abgeordneter")
                .append("DDCKategorien", new Document("$push", "$mainTopic"))
                .append("maybeKategorien", new Document("$push", "$maybeTopic")));
        Bson projectTest = project(new Document("allDDC", new Document("$concatArrays", Arrays.asList("$DDCKategorien", "$maybeKategorien"))));

        List<Bson> pipeline = new ArrayList<>(Arrays.asList(lookup, unwind, project, group, projectTest));

        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> speakerWithDDCJSON.put(procBlock.getString("_id"),
                        ((ArrayList<String>) procBlock.get("allDDC"))));
        System.out.println(speakerWithDDCJSON);
        return speakerWithDDCJSON;
    }

    /**
     * find all speeches that follow the search pattern*
     *
     * @param textFilter
     * @param dateFilterOne
     * @param dateFilterTwo
     * @author Edvin Nise
     */
    @Unfinished("Need to know what data we will need for the visualisation")
    public ArrayList<JSONObject> findSpeech(String textFilter, String dateFilterOne, String dateFilterTwo,
                                            String fractionFilter, String personFilter) {
        Bson match = match(new Document("$text", new Document("$search", textFilter)));
        Bson project = project(new Document("_id", 1));
        List<Bson> pipeline = new ArrayList<>(Arrays.asList(project));

        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!dateFilterOne.isEmpty()) {
            applyDateFiltersToAggregation(pipeline, dateFilterOne, dateFilterTwo);
        }
        if (!fractionFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, fractionFilter, "");
        }
        if (!personFilter.isEmpty()) {
            applyPersonFractionFiltersToAggregation(pipeline, "", personFilter);
        }

        pipeline.add(0, match);
        ArrayList<JSONObject> objList = new ArrayList<>();
        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock -> {
                    JSONObject obj = new JSONObject();
                    obj.put("speechID", procBlock.getString("_id"));
                    objList.add(obj);
                });
        System.out.println(objList);
        return objList;
    }

    /**
     * returns all required Data for visualisation of a speech
     *
     * @param redeID
     * @author Edvin Nise
     */
    public JSONObject allSpeechData(String redeID) {
        Bson match = match(new Document("_id", new Document("$eq", redeID)));
        Bson lookupSpeaker = lookup("person", "speakerID", "_id", "speaker");
        Bson lookupComments = lookup("comment", "_id", "speechID", "comments");
        Bson unwindSpeaker = new Document("$unwind", new Document("path", "$speaker")
                .append("preserveNullAndEmptyArrays", true));
        Bson unwindComments = new Document("$unwind", new Document("path", "$comments")
                .append("preserveNullAndEmptyArrays", true));
        Bson lookupCommentator = lookup("person", "comments.commentatorID", "_id", "CommentatorData");
        Bson unwindCommentatorData = new Document("$unwind", new Document("path", "$CommentatorData")
                .append("preserveNullAndEmptyArrays", true));
        Bson limit = limit(1);

        List<Bson> pipeline = new ArrayList<>(Arrays.asList(match, lookupSpeaker, lookupComments, unwindSpeaker
                ,lookupCommentator,unwindCommentatorData, limit));

        JSONObject obj = new JSONObject();

        db.getCollection("speech").aggregate(pipeline)
                .allowDiskUse(false)
                .forEach((Consumer<? super Document>) procBlock ->
                {

                    obj.put("speechID", procBlock.getString("_id"));
                    obj.put("speakerID", procBlock.getString("speakerID"));
                    obj.put("text", procBlock.getString("text"));
                    obj.put("speechSentiment", procBlock.getDouble("sentiment"));
                    obj.put("sentences", procBlock.get("sentences"));
                    obj.put("namedEntitiesPer", procBlock.get("namedEntitiesPer"));
                    obj.put("namedEntitiesLoc", procBlock.get("namedEntitiesLoc"));
                    obj.put("namedEntitiesOrg", procBlock.get("namedEntitiesOrg"));
                    obj.put("date", TimeHelper.mongoDateToGermanDate(procBlock.getDate("date")));
                    obj.put("speaker", procBlock.get("speaker"));
                    obj.put("comment", procBlock.get("comments"));
                    obj.put("CommentatorData", procBlock.get("CommentatorData"));

                });
        System.out.println(obj);
        return obj;
    }

    /**
     * returns all poll results from all named fractions
     * @author Edvin Nise
     */
    @Unfinished("Dont know where we save this data")
    public ArrayList<JSONObject> getPollResults() {

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
        ArrayList<JSONObject> objList = new ArrayList<>();
        db.getCollection("poll").aggregate(pipeline).allowDiskUse(false).forEach((Consumer<? super Document>) procBlock -> {
            JSONObject obj = new JSONObject();
            obj.put("totalVotes", procBlock.getInteger("totalVotes"));
            obj.put("pollID", procBlock.getString("_id"));
            obj.put("totalVotesYes", procBlock.getInteger("totalVotesYes"));
            obj.put("totalVotesNo", procBlock.getInteger("totalVotesNo"));
            obj.put("totalVotesAbstained", procBlock.getInteger("totalVotesAbstained"));
            obj.put("totalVotesNoVotes", procBlock.getInteger("totalVotesNoVotes"));

            if (!procBlock.getInteger("totalVotesSPD").equals(0)) {
                JSONObject objSPD = new JSONObject();
                objSPD.put("totalVotesSPD", procBlock.getInteger("totalVotesSPD"));
                objSPD.put("SPDYes", procBlock.getInteger("SPDYes"));
                objSPD.put("SPDNo", procBlock.getInteger("SPDNo"));
                objSPD.put("SPDAbstained", procBlock.getInteger("SPDAbstained"));
                objSPD.put("SPDNoVotes", procBlock.getInteger("SPDNoVotes"));
                obj.put("SPDresults", objSPD);
            }

            if (!procBlock.getInteger("totalVotesAfD").equals(0)) {
                JSONObject objAfD = new JSONObject();
                objAfD.put("totalVotesAfD", procBlock.getInteger("totalVotesAfD"));
                objAfD.put("AfDYes", procBlock.getInteger("AfDYes"));
                objAfD.put("AfdNo", procBlock.getInteger("AfdNo"));
                objAfD.put("AfDAbstained", procBlock.getInteger("AfDAbstained"));
                objAfD.put("AfDNoVotes", procBlock.getInteger("AfDNoVotes"));
                obj.put("AfDresults", objAfD);
            }

            if (!procBlock.getInteger("totalVotesCxU").equals(0)) {
                JSONObject objCxU = new JSONObject();
                objCxU.put("totalVotesCxU", procBlock.getInteger("totalVotesCxU"));
                objCxU.put("CxUYes", procBlock.getInteger("CxUYes"));
                objCxU.put("CxUNo", procBlock.getInteger("CxUNo"));
                objCxU.put("CxUAbstained", procBlock.getInteger("CxUAbstained"));
                objCxU.put("CxUNoVotes", procBlock.getInteger("CxUNoVotes"));
                obj.put("CxUresults", objCxU);
            }

            if (!procBlock.getInteger("totalVotesB90").equals(0)) {
                JSONObject objB90 = new JSONObject();
                objB90.put("totalVotesB90", procBlock.getInteger("totalVotesB90"));
                objB90.put("B90Yes", procBlock.getInteger("B90Yes"));
                objB90.put("B90No", procBlock.getInteger("B90No"));
                objB90.put("B90Abstained", procBlock.getInteger("B90Abstained"));
                objB90.put("B90NoVotes", procBlock.getInteger("B90NoVotes"));
                obj.put("B90results", objB90);
            }

            if (!procBlock.getInteger("totalVotesFDP").equals(0)) {
                JSONObject objFDP = new JSONObject();
                objFDP.put("totalVotesFDP", procBlock.getInteger("totalVotesFDP"));
                objFDP.put("FDPYes", procBlock.getInteger("FDPYes"));
                objFDP.put("FDPNo", procBlock.getInteger("FDPNo"));
                objFDP.put("FDPAbstained", procBlock.getInteger("FDPAbstained"));
                objFDP.put("FDPNoVotes", procBlock.getInteger("FDPNoVotes"));
                obj.put("FDPresults", objFDP);
            }

            if (!procBlock.getInteger("totalVotesLINKE").equals(0)) {
                JSONObject objLINKE = new JSONObject();
                objLINKE.put("totalVotesLINKE", procBlock.getInteger("totalVotesLINKE"));
                objLINKE.put("LINKEYes", procBlock.getInteger("LINKEYes"));
                objLINKE.put("LINKENo", procBlock.getInteger("LINKENo"));
                objLINKE.put("LINKEAbstained", procBlock.getInteger("LINKEAbstained"));
                objLINKE.put("LINKENoVotes", procBlock.getInteger("LINKENoVotes"));
                obj.put("LINKEresults", objLINKE);
            }

            if (!procBlock.getInteger("totalVotesindependent").equals(0)) {
                JSONObject objindependent = new JSONObject();
                objindependent.put("totalVotesindependent", procBlock.getInteger("totalVotesindependent"));
                objindependent.put("independentYes", procBlock.getInteger("independentYes"));
                objindependent.put("independentNo", procBlock.getInteger("independentNo"));
                objindependent.put("independentAbstained", procBlock.getInteger("independentAbstained"));
                objindependent.put("independentNoVotes", procBlock.getInteger("independentNoVotes"));
                obj.put("independentresults", objindependent);
            }


            objList.add(obj);
        });
        System.out.println(objList);
        return objList;
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
     *
     * @param col
     * @param field
     * @author Edvin Nise
     */
    public void dropTextIndex(String col, String field) {
        db.getCollection(col).dropIndex(field + "_text");
    }
}
