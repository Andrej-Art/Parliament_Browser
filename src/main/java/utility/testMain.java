package utility;

import java.io.IOException;

public class testMain {
    public static void main(String[] args) throws IOException {
        MongoDBHandler testMongo = new MongoDBHandler();
       // testMongo.getSpeechesBySpeakerCount();
        //testMongo.getTokenCount();
        testMongo.getPersonEntities();
        testMongo.getLocationEntities();
        testMongo.getOrganisationEntities();
    }
}
