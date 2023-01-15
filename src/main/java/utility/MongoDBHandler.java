package utility;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import data.Speech;
import data.impl.AgendaItem_Impl;
import data.impl.Comment_Impl;
import data.impl.Person_Impl;
import data.impl.Speech_Impl;
import org.bson.Document;
import org.bson.conversions.Bson;
import utility.annotations.*;
import utility.uima.MongoNamedEntity;
import utility.uima.MongoSentence;
import utility.uima.MongoToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
     * Basic method to get a collection through the MongoDBHandler
     * @param col
     * @return the Collection
     * @author DavidJordan
     */
    public MongoCollection<Document> getCollection(String col){
        return db.getCollection(col);
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
     * @param id the _id of the specified Document
     * @param collection the collection name
     * @return the found Document
     * @author DavidJordan
     */
    public Document getDocument(String id, String collection){
        Document document = new Document();
        try {
            Document queryDoc = new Document().append("_id", id);
            for (Document value : this.getCollection(collection).find(queryDoc)) {
                document = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Basic Method to create a collection
     * @param col collection name
     * @return true if it was created
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

    public void deleteDocument(String col, String id){
        if(checkIfDocumentExists(col, id)){
            this.getCollection(col).deleteOne(Filters.eq("_id", id));
        }
        else {
            System.out.println(" Document  with _id:" + " id was not found in collection: " + col);
        }
    }

    /**
     * Method to convert a List of Java Person_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     * @param persons
     * @author DavidJordan
     */
    @Unfinished("Reason")
    public void insertPersons(List<Person_Impl> persons) {
        ArrayList<Document> mongoPersons = new ArrayList<>(0);
        for (Person_Impl person : persons) {
            mongoPersons.add(Document.parse(gson.toJson(person)));
        }
        this.getCollection("person").insertMany(mongoPersons);
    }


    /**
     * Method to insert a speech and its associated NLP data into three seperate collections into the MongoDB.
     * @param speech The Java object of the speech
     * @param fullCas the full CAS String of th NLP Analysis
     * @param tokens The List of MongoTokens of the speech
     * @param sentences the List of MongoSentences
     * @param namedEntities The List of MongoNamedEntities
     * @param sentiment the Speech sentiment value
     * @param mainTopic the speech's main topic
     * @author DavidJordan
     */
    @Unfinished("Reason")
    public void insertSpeech(
            Speech speech,
            String fullCas,
            List<MongoToken> tokens,
            List<MongoSentence> sentences,
            List<MongoNamedEntity> namedEntities,
            double sentiment,
            String mainTopic) {
        // Converting the Named Entities into serialised JSON Strings according to their type
        List<Document> namedEntitiesPER = new ArrayList<>(0);
        List<Document> namedEntitiesLOC = new ArrayList<>(0);
        List<Document> namedEntitiesORG = new ArrayList<>(0);
        for(MongoNamedEntity namedEntity: namedEntities){
            switch (namedEntity.getEntityType()) { // no default necessary
                case "PER":
                    namedEntitiesPER.add(Document.parse(gson.toJson(namedEntity))); break;
                case "LOC":
                    namedEntitiesLOC.add(Document.parse(gson.toJson(namedEntity))); break;
                case "ORG":
                    namedEntitiesORG.add(Document.parse(gson.toJson(namedEntity))); break;
            }
        }
        // Converting the sentences into a Document to insert into the "sentences" field
        List<Document> sentencesDocs = new ArrayList<>(0);
        for(MongoSentence mongoSentence: sentences){
            sentencesDocs.add(Document.parse(gson.toJson(mongoSentence)));
        }
        // Creating the speech document to insert into the DB
        Document speechDocument = new Document()
                .append("_id", speech.getID())
                .append("speaker_id", speech.getSpeakerID())
                .append("text", speech.getText())
                .append("date", speech.getDate())
                .append("sentences", sentencesDocs)
                .append("sentiment", sentiment)
                .append("main_topic", mainTopic)
                .append("named_entities_per", namedEntitiesPER)
                .append("named_entities_org", namedEntitiesORG)
                .append("named_entities_loc", namedEntitiesLOC);
        //Inserting finished speech document into the speech collection
        this.getCollection("speech").insertOne(speechDocument);

        // Inserting the full speech CAS into seperate collection
        Document speechCasDoc = new Document()
                .append("_id", speech.getID())
                .append("full_cas", fullCas);
        //Inserting into speech_cas collection
        this.getCollection("speech_cas").insertOne(speechCasDoc);

        //Serialising the tokens
        List<Document> speechTokensDocs = new ArrayList<>(0);
        for (MongoToken mongoToken: tokens){
            speechTokensDocs.add(Document.parse(gson.toJson(mongoToken)));
        }
        // Adding to speech_tokens collection
        Document speechTokenDoc = new Document()
                .append("_id", speech.getID())
                .append("speaker_id", speech.getSpeakerID())
                .append("date", speech.getDate())
                .append("tokens", speechTokensDocs);
    }

    /**
     * Method to convert a List of Java AgendaItem_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     * @param agendaItems
     * @author DavidJordan
     */
    public void insertAgendaItems(List<AgendaItem_Impl> agendaItems) {
        ArrayList<Document> mongoAgendaItems = new ArrayList<>(0);
        for (AgendaItem_Impl agendaItem : agendaItems) {
            mongoAgendaItems.add(Document.parse(gson.toJson(agendaItem)));
        }
        this.getCollection("agendaItem").insertMany(mongoAgendaItems);
    }

    /**
     * Method to convert a List of Java Person_Impl object to BSON format using Gson to serialise them
     * and then insert them into the database
     * @param comments
     * @author DavidJordan
     */
    public void insertComments(List<Comment_Impl> comments) {
        ArrayList<Document> mongoComments = new ArrayList<>(0);
        for (Comment_Impl comment : comments) {
            mongoComments.add(Document.parse(gson.toJson(comment)));
        }
        this.getCollection("comment").insertMany(mongoComments);
    }

    /**
     * Method to update a speech document in the DB with a speech Java object as parameter.
     * @param speech
     * @return boolean to show if update was successful
     * @author DavidJordan
     */
    @Testing
    public boolean update(Speech_Impl speech){
        Document speechQuery = new Document().append("_id", speech.getID());
        Document newSpeech = Document.parse(gson.toJson(speech));

        try {
            this.getCollection("speech").replaceOne(speechQuery, newSpeech);
            return true;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return false;
    }


    /**
     * Updates a Document in the database, by replacing it with the given Document
     * @param document the document that will replace the Document with the specified id
     * @param collection  the collection to get from the database
     * @param id  the id of the Document to be replaced
     * @return boolean true if update was sucessful false if not
     * @author DavidJordan
     */
    public boolean update(Document document, String collection, String id){

        if(!checkIfDocumentExists(collection, id)){
            System.out.println("Unable to perform update, because the target Document with id: " + id +  " does not exist in col: " + collection);
            return false;
        }
        else{
            try {
                this.getCollection(collection).replaceOne(eq("_id", id), document);
                return true;
            } catch (Exception e) {
                System.err.println("Update could not be performed. Invalid input.");
                e.printStackTrace();
                return false;
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
     * returns all Speakers with their speeches count.
     * @author Edvin Nise
     */
    public void getSpeechesBySpeakerCount() {
        Bson groupSpeaker = group(new Document("rednerID", "$rednerID"),
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


}
