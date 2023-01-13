package utility;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
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
import utility.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * When instanced, the {@code MongoDBHandler} connects to this group's MongoDB.
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
     * Untested Method to insert a List of Speech_Impl objects into the db
     * I still have to determine if GSON.toJson correctly
     * assigns the _id of the Java Object to the "_id" field in the DB. In the past I always did the conversion manually.
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
     * Untested Method to insert a List of Person_Impl objects into the db
     *  I still have to determine if GSON.toJson correctly
     *  assigns the _id of the Java Object to the "_id" field in the DB. In the past I always did the conversion manually.
     *
     * @param persons
     * @author DavidJordan
     *
     */
    @Unfinished
    @Testing
    public void insertPersons(List<Person_Impl> persons) {
        Gson gson = new Gson();
        ArrayList<Document> mongoPersons = new ArrayList<>(0);
        for (Person_Impl person : persons) {
            mongoPersons.add(Document.parse(gson.toJson(person)));
        }
        this.getCollection("test_person2").insertMany(mongoPersons);
    }

    @Unfinished
    @Testing
    public void insertAgendaItems(List<AgendaItem_Impl> agendaItems) {
        Gson gson = new Gson();
        ArrayList<Document> mongoAgendaItems = new ArrayList<>(0);
        for (AgendaItem_Impl agendaItem : agendaItems) {
            mongoAgendaItems.add(Document.parse(gson.toJson(agendaItem)));
        }
        this.getCollection("agendaItem").insertMany(mongoAgendaItems);
    }

    @Unfinished
    @Testing
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
     * @param speech
     * @return
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





    }


