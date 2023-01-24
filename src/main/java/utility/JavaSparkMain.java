package utility;
import spark.Spark;
import utility.annotations.Testing;
import utility.annotations.Unfinished;
import static spark.Spark.*;


/**
 * Main Class for Starting the API Server
 * @author Andrej Artuschenko
 */
@Testing

public class JavaSparkMain {

    // connection to database
    //private static MongoDBHandler mongoDBHandler = new MongoDBHandler();

    /**
     * main method to start the API server and build Routes
     * @author Andrej Artuschenko
     */

    public static void main(String args[]){


        get("/speakers", (req,res)-> "Hello World");

        get("/speechesinfos", (req, res)-> "Hello World");





    }
}
