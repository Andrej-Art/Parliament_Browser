package utility;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import data.Speech;
import data.impl.AgendaItem_Impl;
import data.impl.Comment_Impl;
import data.impl.Person_Impl;
import data.impl.Speech_Impl;
import org.bson.Document;
import com.mongodb.client.*;
import org.bson.Document;
import utility.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.mongodb.client.model.Filters.*;

/**
 * When instanced, the {@code MongoDBHandler} connects to the MongoDB specified in {@code PRG_WiSe22_Group_9_4.txt}.
 * All methods which manipulate or query data in the database are found here.
 * @author Eric Lakhter
 * @author DavidJordan
 */
@Unfinished
public class MongoDBHandler {
    private final MongoDatabase db;

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
     * Basic method to get a collection throught the MongoDBHandler
     * @param col
     * @return the Collection
     * @author DavidJordan
     */
    public MongoCollection<Document> getCollection(String col){
        return db.getCollection(col);
    }

    /**
     * Basic method to check whether a given collection already exists in the Database
     * @param col
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
     * Basic Method to create a collection
     * @param col
     * @return true if it was created
     * @author DavidJordan
     */
    public boolean createCollection(String col){
        if(!collectionExists(col)){
            db.createCollection(col);
            return true;
        }
        return false;
    }

    /**
     * Untested Method to insert a List of Person_Impl objects into the db
     *  I still have to determine if GSON.toJson correctly
     *  assigns the id to the "_id" field in the DB. In the past I always did the conversion manually.
     *
     * @param persons
     * @author DavidJordan
     *
     */
    @Unfinished
    public void insertPersons(List<Person_Impl> persons) {
        Gson gson = new Gson();
        ArrayList<Document> mongoPersons = new ArrayList<>(0);
        for (Person_Impl person : persons) {
            mongoPersons.add(Document.parse(gson.toJson(person)));
        }
        this.getCollection("person").insertMany(mongoPersons);
    }


    /**
     * Method to insert a List of Speech_Impl objects into the db in serialised form. WITHOUT the UIMA fields sofar.
     * They will either be added by another method or we'll have to use something else than the suggested method below.
     *
     *
     * @param speeches
     * @author DavidJordan
     */
    @Unfinished
    public void insertSpeeches(List<Speech_Impl> speeches){
        Gson gson = new Gson();
        ArrayList<Document>  mongoSpeeches = new ArrayList<>(0);
        for(Speech_Impl speech : speeches){
            mongoSpeeches.add(Document.parse(gson.toJson(speech)));
        }
        this.getCollection("speech").insertMany(mongoSpeeches);
    }



    /**
     * Method to insert a list of agendaItems, using GSON to serialise the AgItem Objects.
     * @param agendaItems
     * @author DavidJordan
     */
    public void insertAgendaItems(List<AgendaItem_Impl> agendaItems) {
        Gson gson = new Gson();
        ArrayList<Document> mongoAgendaItems = new ArrayList<>(0);
        for (AgendaItem_Impl agendaItem : agendaItems) {
            mongoAgendaItems.add(Document.parse(gson.toJson(agendaItem)));
        }
        this.getCollection("agendaItem").insertMany(mongoAgendaItems);
    }

    /**
     * Method to insert a list of comments, using GSON to serialise the Comment Objects.
     * @param comments
     * @author DavidJordan
     */
    public void insertComments(List<Comment_Impl> comments) {
        Gson gson = new Gson();
        ArrayList<Document> mongoComments = new ArrayList<>(0);
        for (Comment_Impl comment : comments) {
            mongoComments.add(Document.parse(gson.toJson(comment)));
        }
        this.getCollection("comment").insertMany(mongoComments);
    }

    /**
     * TODO // It needs to be decided between us when and how the UIMA fields are added to the collection. Since
     *   at the moment we only insert without UIMA fields.
     *
     * Method to update a speech document in the DB with a speech Java object as parameter.
     * @param speech
     * @return boolean to show if update was successful
     */
    @Unfinished
    @Testing
    public boolean update(Speech_Impl speech){
        Gson gson = new Gson();
        Document speechQuery = new Document().append("_id", speech.getID());

        Document newSpeech = Document.parse(gson.toJson(speech));

        try {
            this.getCollection("speech").replaceOne(speechQuery, newSpeech);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
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
}
