package utility;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import data.*;
import exceptions.WrongInputException;
import org.bson.Document;
import org.bson.conversions.Bson;
import utility.annotations.*;
import utility.uima.ProcessedSpeech;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * When instanced, the {@code MongoDBHandler} connects to the MongoDB specified in {@code PRG_WiSe22_Group_9_4.txt}.
 * All methods which manipulate or query data in the database are found here.
 * @author Eric Lakhter
 * @author DavidJordan
 */
@Unfinished("This class is unfinished")
public class MongoDBHandler {
    private final MongoDatabase db;
    private final InsertManyOptions imo =  new InsertManyOptions().ordered(false);
    private final Gson gson = new Gson();

    /**
     * Connects to the MongoDB specified in {@code PRG_WiSe22_Group_9_4.txt}.
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
     * @return The connected database.
     * @author Eric Lakhter
     */
    @Testing
    public MongoDatabase getDB() {
        return db;
    }

    /**
     * Basic method to check whether a given collection already exists in the Database
     * @param col collection name
     * @return true if it exists
     * @author DavidJordan
     */
    public boolean collectionExists(String col){
        for(String colName : db.listCollectionNames()){
            if (colName.equals(col)){
                return true;
            }
        }
        return false;
    }


    /**
     * Gets a Document with the specified id from the specified collection
     * @param col the collection name
     * @param id the _id of the specified Document
     * @return the found Document
     * @author DavidJordan
     */
    public Document getDocument(String col, String id){
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
     * @param col collection name
     * @author DavidJordan
     */
    public void createCollection(String col){
        if(!collectionExists(col)){
            db.createCollection(col);
            System.out.println("Collection " + col + "was created.");
        }
        else{
            System.out.println("Collection " + col + "was not created because it already exists.");
        }
    }

    /**
     * Method to delete a Document with specified id in a specified collection.
     * @param col the target collection's name
     * @param id the id of the document to delete
     * @author David Jordan
     */
    public void deleteDocument(String col, String id){
        if(checkIfDocumentExists(col, id)){
            db.getCollection(col).deleteOne(Filters.eq("_id", id));
        }
        else {
            System.out.println(" Document  with _id:" + " id was not found in collection: " + col);
        }
    }

    /**
     * Method to convert a List of Java Person_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     * @param persons the List of Persons to add to the database
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertPersons(List<Person> persons) throws WrongInputException {
        if (persons == null || persons.isEmpty()){
            throw new WrongInputException("Input is null or empty.");
        }
        ArrayList<Document> mongoPersons = new ArrayList<>(0);
        for (Person person : persons) {
            if(person == null){
                throw new IllegalArgumentException("protocol is null.");
            }
            mongoPersons.add(Document.parse(gson.toJson(person)));
        }
        try {
            db.getCollection("person").insertMany(mongoPersons);
        } catch (MongoWriteException e) {
            System.err.println("Insert of person failed.");
        }
    }

    /**
     * Method to insert a single Person object into the database in serialised form
     * @param person The Person Object
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertPerson(Person person) throws WrongInputException {
        if(person == null){
            throw new WrongInputException("person is null. Input failed.");
        }
       Document personDoc =  Document.parse(gson.toJson(person));

        try {
            db.getCollection("person").insertOne(personDoc);
        } catch (MongoWriteException e) {
            System.err.println("Insert of person failed.");
        }
    }

    /**
     * Method to insert a single Protocol into the database in serialised form.
     * @param protocol The Protocol Object to be inserted
     * @throws WrongInputException
     */
    public void insertProtocol(Protocol protocol) throws WrongInputException {
        if (protocol == null){
            throw new WrongInputException("Protocol is null. Input failed.");
        }
        Document protocolDoc = Document.parse(gson.toJson(protocol));

        try {
            db.getCollection("protocol").insertOne(protocolDoc);
        } catch (MongoWriteException e) {
            System.err.println("Insert of protocol failed.");
        }
    }

    /**
     * Method to insert a list of Protocol Object into the database in serilised form.
     * @param protocols the ArrayList of Protocol Objects
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertProtocols(List<Protocol> protocols) throws WrongInputException {
        if (protocols == null || protocols.isEmpty()){
            throw new WrongInputException("Input is null or empty.");
        }
        ArrayList<Document> protocolDocs = new ArrayList<>(0);
        for(Protocol protocol: protocols){
            if(protocol == null){
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
     * @param id The Username of the User
     * @param password  The password of the user
     * @param rights  The rights that the user has when using his account
     * @author DavidJordan
     */
    public void insertUser(String id, String password, String rights){
        //Create the user Document
        Document userDoc = new Document()
                .append("_id", id)
                .append("password", password)
                .append("rights", rights);
        // Insert it into the DB
        try {
            db.getCollection("user").insertOne(userDoc);
        } catch (MongoWriteException e) {
            System.err.println("User: " + id + " could not be inserted.");
        }
    }



    /**
     * Method to insert a single ProcessedSpeech Object into the database. Inserts a different representation
     * into speech, speech_cas, and speech_tokens  collections respectively.
     * @param processedSpeech
     * @author DavidJordan
     */
    public void insertSpeech(ProcessedSpeech processedSpeech){
        //Insert single processedSpeech into speech collection
        try {db.getCollection("speech").insertOne(Document.parse(processedSpeech.toSpeechJson()));}
        catch(MongoWriteException ignored){}

        //Insert single document into speech_cas collection
        try {db.getCollection("speech_cas").insertOne(Document.parse(processedSpeech.toSpeechJson()));}
        catch(MongoWriteException ignored){}

        //Insert single document into speech_tokens collection
        try {db.getCollection("speech_tokens").insertOne(Document.parse(processedSpeech.toSpeechJson()));}
        catch(MongoWriteException ignored){}
    }

    /**
     * Inserts a list of processed speeches into the DB.
     * @param processedSpeeches List of processed speeches.
     * @author DavidJordan
     * @author Eric_Lakhter
     */
    public void insertSpeeches(List<ProcessedSpeech> processedSpeeches) {

        List<Document> speechDocs = new ArrayList<>(0);
        List<Document> speechCasDocs = new ArrayList<>(0);
        List<Document> speechTokenDocs = new ArrayList<>(0);

        for(ProcessedSpeech processedSpeech: processedSpeeches){
            //parse into Bson Document and add to list
            speechDocs.add(Document.parse(processedSpeech.toSpeechJson()));

            //parse into Bson Document for speech_cas and add to list
            speechCasDocs.add(Document.parse(processedSpeech.toSpeechCasJson()));

            //parse into Bson Document for speech_tokens collection and add to list
            speechTokenDocs.add(Document.parse(processedSpeech.toSpeechTokensJson()));
        }

        // MongoBulkWriteExceptions are caught when inserting the Lists
        try {db.getCollection("speech").insertMany(speechDocs, imo);}
        catch (MongoBulkWriteException ignored) {}
        try {db.getCollection("speech_cas").insertMany(speechCasDocs, imo);}
        catch (MongoBulkWriteException ignored) {}
        try {db.getCollection("speech_token").insertMany(speechTokenDocs, imo);}
        catch (MongoBulkWriteException ignored) {}

    }

    /**
     * Method to convert a List of Java AgendaItem_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     * @param agendaItems the List of
     * @author DavidJordan
     * @throws WrongInputException
     */
    public void insertAgendaItems(List<AgendaItem> agendaItems) throws WrongInputException {
        if(agendaItems == null || agendaItems.isEmpty()){
            throw new WrongInputException("Input is null or empty");
        }
        ArrayList<Document> mongoAgendaItems = new ArrayList<>(0);
        for (AgendaItem agendaItem : agendaItems) {
            if(agendaItem == null){
                throw new IllegalArgumentException("An agendaItem object is null.");
            }
            mongoAgendaItems.add(Document.parse(gson.toJson(agendaItem)));
        }
        try {
            db.getCollection("agendaItem").insertMany(mongoAgendaItems);
        } catch (MongoWriteException e) {
            System.err.println("Insert of agendaItems failed.");
        }
    }


    /**
     * Method to insert a comment and its sentiment value into the "comment" collection
     * of the database.
     * @param comment The comment Object
     * @param sentiment The sentiment value of the comment
     * @throws WrongInputException
     * @author DavidJordan
     */
    public void insertComment(Comment comment, double sentiment) throws WrongInputException {
        if(comment == null){
            throw new WrongInputException("comment is null");
        }
        //Create the  Comment Document
        Document commentDoc = new Document()
                .append("_id", comment.getID())
                .append("speech_id", comment.getSpeechID())
                .append("speaker_id", comment.getSpeakerID())
                .append("commentator_id", comment.getCommentatorID())
                .append("text", comment.getText())
                .append("date", comment.getDate())
                .append("sentiment", sentiment);
        // Insert it into the comment collection

        try {
            db.getCollection("comment").insertOne(commentDoc);
        } catch (MongoWriteException e) {
            System.err.println("Insert of comment with id: " + comment.getID() + "could not be performed.");
        }
    }

    /**
     * Inserts a list of polls into the {@code poll} collection.
     * @param polls List of Poll objects.
     * @author Eric Lakhter
     */
    public void insertPolls(List<Poll> polls) {
        List<Document> pollDocs = new ArrayList<>();
        for (Poll poll : polls) {
            pollDocs.add(Document.parse(poll.toJson()));
        }
        try {db.getCollection("poll").insertMany(pollDocs, imo);}
        catch (MongoBulkWriteException ignored) {}
    }

    /**
     * Updates a Document in the database, by replacing it with the given Document
     * @param document the document that will replace the Document with the specified id
     * @param collection  the collection to get from the database
     * @param id  the id of the Document to be replaced
     * @return boolean true if update was successful, false if not
     * @author DavidJordan
     */
    public boolean updateDocument(Document document, String collection, String id){

        if(!checkIfDocumentExists(collection, id)){
            System.out.println("Unable to perform update, because the target Document with id: " + id +  " does not exist in col: " + collection);
            return false;
        }
        else{
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
     * @param document
     * @param collection
     * @author DavidJordan
     */
    public void addDocument(Document document, String collection){
        if(collectionExists(collection)){
            try {
                db.getCollection(collection).insertOne(document);
            } catch (MongoWriteException e) {
                System.err.println("Failed to add Document to collection.");
            }
        }
    }

    /**
     * Adds a documents to an existing collection
     * @param documents
     * @param collection
     * @author DavidJordan
     */
    public void addDocuments(List<Document> documents, String collection){
        if(collectionExists(collection) && documents != null){
            try {
                db.getCollection(collection).insertMany(documents);
            } catch (MongoWriteException e) {
                System.err.println("Failed to add Documents to collection.");
            }
        }
    }


    /**
     * Adds potential date filters in front of an aggregation pipeline.
     * @param pipeline The pipeline to be modified.
     * @param dateFilterOne If {@code (dateFilterTwo.isEmpty() == true)} this is a specific date,
     *                      else it's the lower bound for a date range to be filtered for.
     * @param dateFilterTwo Higher bound for dates to be filtered for. Gets ignored if {@code (dateFilterOne.isEmpty() == true)}.
     *                      <p>If {@code (dateFilterTwo < dateFilterOne)} then the query result will be empty.
     * @author Eric Lakhter
     * @modified DavidJordan
     */
    public void applyDateFiltersToAggregation(List<Bson> pipeline, String dateFilterOne, String dateFilterTwo) {
        if (!dateFilterOne.isEmpty()) {
            Bson matchDate = dateFilterTwo.isEmpty() ?
                    match(new Document("datum", dateFilterOne)) :
                    match(and(Arrays.asList(gte("datum", dateFilterOne), lte("datum", dateFilterTwo))));
            pipeline.add(0, matchDate);
        }
    }

    @Unfinished("Probably, needs to be adapted to the fraction 19, fraction 20 options." +
            " Possibly also needs to be changed to fit the needs of the visualisation in the front end.")
    /**
     *
     * Adds potential person/fraction filters in front of a pipeline performed on either the speech or comment collection.
     * If both person and fraction filters exist, person has priority while fraction gets ignored.
     * <p> If enabled, the project stage leaves the pipeline with a collection containing the
     * {@code _id, speechID, speakerID, neededFieldOne, neededFieldTwo} and if filtering for fractions a
     * {@code persondata} field matching the {@code speakerID} form the person collection.
     * @param pipeline The pipeline to be modified.
     * @param personFilter The person ID to be filtered for.
     * @param fractionFilter the fraction to be filtered for.
     * @param neededField Potential field names to be added to the projection. No projection will be performed if
     *                    no needed fields are given.
     * @author Eric Lakhter
     * @modified DavidJordan
     */
    public void applyPersonFractionFiltersToAggregation(List<Bson> pipeline, String personFilter, String fractionFilter, String... neededField) {
        Document projectDoc = new Document("speechID", 1).append("speakerID", 1);
        // Setting each of the needed fields to be included in the results
        for (String field : neededField) {
            projectDoc.append(field, 1);
        }

        Bson project = project(projectDoc);

        if (!personFilter.isEmpty()) {
            pipeline.add(0, match(new Document("speakerID", personFilter)));
        } else if (!fractionFilter.isEmpty()) {
            pipeline.add(0, match(new Document("persondata.fraction", fractionFilter)));
            pipeline.add(0, unwind("$persondata"));
            pipeline.add(0, lookup("person", "speakerID", "_id", "persondata"));
        }

        if (neededField.length != 0) {
            pipeline.add(0, project);
        }
    }



    /**
     * Returns the text belonging to either a speech or comment ID.
     * @param col Collection to collect from. Must be either {@code speech} or {@code comment}.
     * @param id The ID to look for.
     * @return {@code text} String.
     * @author Eric Lakhter
     */
    public String getText(String col, String id) throws NullPointerException {
        Document result = db.getCollection(col).find(new Document("_id", id)).iterator().tryNext();
        if (result == null) throw new NullPointerException("The Document with _id = " + id + " does not exist in this collection.");
        return result.getString("text");
    }

    /**
     * Adds a full CAS XML String to a collection.
     * @param col Collection to insert in. Must be either {@code speech_cas} or {@code comment_cas}.
     * @param id Speech/Comment ID.
     * @param cas The CAS String.
     * @author Eric Lakhter
     */
    public void addCAS(String col, String id, String cas) {
        db.getCollection(col).insertOne(new Document("_id", id).append("cas", cas));
    }

    /**
     * Checks if a Document specified by the {@code id} has a given {@code field}.
     * @param col Collection to search in.
     * @param id Document key.
     * @param field Field name.
     * @return {@code true} if the field exists, {@code false} otherwise.
     * @author Eric Lakhter
     */
    public boolean checkIfHasNonEmptyField(String col, String id, String field) {
        return db.getCollection(col).find(and(new Document("_id", id), ne(field, null))).iterator().hasNext();
    }

    /**
     * Checks if a Document exists in a collection.
     * @param col Collection to search in.
     * @param id Document key.
     * @return {@code true} if the Document exists, {@code false} otherwise.
     * @author Eric Lakhter
     */
    public boolean checkIfDocumentExists(String col, String id) {
        return db.getCollection(col).find(new Document("_id", id)).iterator().hasNext();
    }

    /**
     * Token ranking for line chart
     * @param ranks Top n tokens
     * @author Eric Lakhter
     */
    public void testPipeline(int ranks){
        Bson unwind = unwind("$tokens");
        Bson group = group("$tokens.lemmaValue", sum("count", 1));
        Bson sort = sort(descending("count"));
//        Bson rankMode = limit(ranks);
        Bson rankMode = match(gte("count", ranks));
        MongoIterable<Document> result = db.getCollection("test_speech_token_edvin")
                .aggregate(Arrays.asList(unwind, group, sort, rankMode));
        for (Document doc : result) {
            System.out.println(doc.toJson());
        }
    }

    /**
     * returns all Speakers with their speeches count.
     * @author Edvin Nise
     */
    public void getSpeechesBySpeakerCount() {
        Bson groupSpeaker = group(new Document("speaker_id", "$speaker_id"),
                sum("SpeechesCount", 1));
        Bson sortDesc = sort(descending("SpeechesCount"));
        db.getCollection("test_speech").aggregate(Arrays.asList(groupSpeaker, sortDesc))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));

    }
    /**
     * returns count of all Tokens
     * @author Edvin Nise
     */
    public void getTokenCount() {
        Bson unwind = Aggregates.unwind("$token");
        Bson groupToken = group(new Document("Token", "$token"),
                sum("tokenCount", 1));
        Bson sortDesc = sort(descending("tokenCount"));
        db.getCollection("test_speech").aggregate(Arrays.asList(unwind, groupToken, sortDesc))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));

    }

    /**
     * returns sorted Person Entities with their respective count
     * @author Edvin Nise
     */
    public void getPersonEntities() {
        Bson unwind = Aggregates.unwind("$personEntity");
        Bson groupPersonEntity = group(new Document("PersonEntity", "$personEntity"),
                sum("PersonEntityCount", 1));
        Bson sortDesc = sort(descending("PersonEntityCount"));
        db.getCollection("test_speech").aggregate(Arrays.asList(unwind, groupPersonEntity, sortDesc))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));
    }
    /**
     * returns sorted Organisation Entities with their respective count
     * @author Edvin Nise
     */
    public void getOrganisationEntities() {
        Bson unwind = Aggregates.unwind("$organisationEntity");
        Bson groupOrganisationEntity = group(new Document("OrganisationEntity", "$organisationEntity"),
                sum("OrganisationEntityCount", 1));
        Bson sortDesc = sort(descending("OrganisationEntityCount"));
        db.getCollection("test_speech").aggregate(Arrays.asList(unwind, groupOrganisationEntity, sortDesc))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));
    }
    /**
     * returns sorted Location Entities with their respective count
     * @author Edvin Nise
     */
    public void getLocationEntities() {
        Bson unwind = Aggregates.unwind("$locationEntity");
        Bson groupLocationEntity = group(new Document("LocationEntity", "$locationEntity"),
                sum("LocationEntityCount", 1));
        Bson sortDesc = sort(descending("LocationEntityCount"));
        db.getCollection("test_speech").aggregate(Arrays.asList(unwind, groupLocationEntity, sortDesc))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));
    }

    /**
     * returns the count for all Parts of Speech
     * @author Edvin Nise
     */
    @Unfinished("waiting for final structure of collection")
    public void getPOSCount() {
        Bson unwind = Aggregates.unwind("$tokenWithPos");
        Bson project = Aggregates.project(new Document("POSValue", new Document("$arrayElemAt", Arrays.asList("$tokenWithPos", 1))));
        Bson groupPOSValue = group(new Document("POSValue", "$POSValue"), sum("POSValueCount", 1));
        db.getCollection("test_speech").aggregate(Arrays.asList(unwind, project, groupPOSValue))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));
    }

    /**
     * returns who commented on which speaker
     * @author Edvin Nise
     */
    @Unfinished("waiting for correct structure of collection")
    public ArrayList<ArrayList<Object>> commentatorToSpeaker() {
        ArrayList<ArrayList<Object>> commentatorToSpeakerData = new ArrayList<>();

        Bson lookupCommentator = lookup("test_person", "commentator_id", "_id", "CommentatorPerson");
        Bson lookupSpeaker = lookup("test_person", "speaker_id", "_id", "SpeakerPerson");
        Bson unwindCommentator = unwind("$CommentatorPerson");
        Bson unwindSpeaker = unwind("$SpeakerPerson");
        Bson project = project(new Document("CommentatorName", "$CommentatorPerson.full_name")
                .append("SpeakerName", "$SpeakerPerson.full_name")
                .append("sentiment", 1));

        // Index [0] = CommentatorName, [1] = SpeakerName, [2] = Comment sentiment
        db.getCollection("test_comment").aggregate(Arrays.asList(lookupCommentator, lookupSpeaker,unwindCommentator, unwindSpeaker, project))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> {
                    ArrayList<Object> data = new ArrayList<>();
                    data.add(procBlock.getString("CommentatorName"));
                    data.add(procBlock.getString("SpeakerName"));
                    data.add(procBlock.getDouble("sentiment"));
                    commentatorToSpeakerData.add(data);
                });
        return commentatorToSpeakerData;
    }

    /**
     * returns Hashmap with Speaker and List of Topics
     * @author Edvin Nise
     */
    @Unfinished("Waiting for structure of collection")
    public HashMap<String, ArrayList<String>> matchSpeakerToDDC() {
        HashMap<String, ArrayList<String>> speakerWithDDCMap = new HashMap<>();

        Bson lookup = lookup("test_person", "speakerID", "_id", "Speaker");
        Bson unwind = unwind("$Speaker");
        Bson project = project(new Document("Abgeordneter", "$Speaker.full_name")
                .append("main_topic" , 1));
        Bson group = new Document("$group", new Document()
                        .append("_id", "$Abgeordneter")
                        .append("DDCKategorien", new Document("$push", "$main_topic")));

        db.getCollection("test_speech_edvin").aggregate(Arrays.asList(lookup, unwind, project, group))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> speakerWithDDCMap.put(procBlock.getString("_id"),  ((ArrayList<String>) procBlock.get("DDCKategorien"))));
        System.out.println(speakerWithDDCMap);
       return speakerWithDDCMap;
    }

    /**
     * find all speeches that follow the search pattern
     * @author Edvin Nise
     * @param filter
     */
    @Unfinished("Need to know what data we will need for the visualisation")
    public void findSpeech(String filter) {
        Bson match = match(new Document("$text", new Document("$search", filter)));
        Bson project = project(new Document("_id", 1));
        db.getCollection("test_speech_edvin").aggregate(Arrays.asList(match, project))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));
    }

    /**
     * returns all required Data for visualisation of a speech
     * @param redeID
     * @author Edvin Nise
     */
    public void allSpeechData(String redeID) {
        Bson match = match(new Document("_id", new Document("$eq", redeID)));
        Bson lookupSpeaker = lookup("test_person", "speakerID", "_id", "Speaker");
        Bson lookupComments = lookup("test_comment", "_id", "speech_id", "comments");
        Bson unwindSpeaker = unwind("$Speaker");
        Bson unwindComments = unwind("$comments");
        Bson lookupCommentator = lookup("test_person", "comments.commentator_id", "_id", "CommentatorData");
        Bson unwindCommentatorData = unwind("$CommentatorData");
        Bson addFieldMergedCommentWithData = new Document("$addFields",
                new Document("CommentatorWithComments",
                        new Document("$mergeObjects", Arrays.asList("$comments", "$CommentatorData"))));
        Bson project = project(new Document("comments", 0)
                .append("CommentatorData", 0));

        db.getCollection("test_speech_edvin").aggregate(Arrays.asList(match, lookupSpeaker, lookupComments, unwindSpeaker,
                unwindComments, lookupCommentator, unwindCommentatorData, addFieldMergedCommentWithData, project))
                .allowDiskUse(false)
                .forEach((Block<? super Document>) procBlock -> System.out.println(procBlock.toJson()));
    }

    /**
     * returns named or unnamed vote results
     * @author Edvin Nise
     */
    @Unfinished("Dont know where we save this data")
    public void getVoteResults() {

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
    public void dropTextIndex(String col, String field){
        db.getCollection(col).dropIndex(field + "_text");
    }

}
