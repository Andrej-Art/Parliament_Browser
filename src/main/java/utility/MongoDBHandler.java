package utility;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import utility.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * When instanced, the {@code MongoDBHandler} connects to this group's MongoDB.
 * All methods which manipulate or query data in the database are found here.
 * @author Eric Lakhter
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
        System.out.println("butter");
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
}
