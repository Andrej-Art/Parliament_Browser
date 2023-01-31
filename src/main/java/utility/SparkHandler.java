package utility;

import freemarker.template.Configuration;
import org.json.JSONObject;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import utility.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * Starts the localhost server for the protocol visualisation.
 * @author Eric Lakhter
 */
@Testing
@Unfinished("Is missing most routes")
public class SparkHandler {
    private static MongoDBHandler mongoDBHandler = null;
    private static final Configuration cfg = Configuration.getDefaultConfiguration();
    // the added string redirects to the /resources/ directory
    private static final String frontendPath = SparkHandler.class.getClassLoader().getResource(".").getPath() + "../../src/main/resources/frontend/";

    public static void main(String[] args) throws IOException {
        SparkHandler.init(new MongoDBHandler());
//        openInDefaultBrowser();
    }

    /**
     * Sets up the website's paths.
     * @see #getHome
     * @author Eric Lakhter
     */
    public static void init(MongoDBHandler mdbh) throws IOException {
        mongoDBHandler = mdbh;
        cfg.setDirectoryForTemplateLoading(new File(frontendPath));
        cfg.setDefaultEncoding("UTF-8");
        port(4567);
        // If there aren't any query params redirect the user to a path with trailing slash
        before((req, res) -> {
            String path = req.pathInfo();
            if (!path.endsWith("/") && req.queryParams().size() == 0) res.redirect(path + "/") ;
        });

        // Test is for testing
        get("/test/", getTest, new FreeMarkerEngine(cfg));

        /*
         * request.queryParams() is a set of query parameters, needs question mark
         * example path:
         * /reden/?id=4&hu=7
         * =>
         * request.queryParams(): [id, hu]     (type: Set<String>)
         * request.queryParams("id"): "4"      (important: potential trailing slash is seen as part of param)
         * request.queryParams("rrr"): null    (no exceptions)
         *
         * ?fraktion=CDU/CSU                   works without issues, even with the slash
         * ?fraktion=BÜNDNIS 90/DIE GRÜNEN     works without issues, spaces get percent-encoded: " " -> "%20"
         * CDU/CSU is equivalent to CDU%2FCSU  %2F is the percent-encoded slash
         */
        get("/", getHome, new FreeMarkerEngine(cfg));

        get("/dashboard/", getDashboard, new FreeMarkerEngine(cfg));

        get("/multi/", getMulti, new FreeMarkerEngine(cfg));
        //get("/dashboard/data/", getChartUpdatesAjax);  // It is not clear how to combine the datefilters and person fraction party filters into one submit

        get("/reden/", getReden, new FreeMarkerEngine(cfg));
        get("/reden/speechVis/", getSpeechVis);
        get("/reden/speechIDs/", getSpeechIDs);

        //trying something else
        get("/update-charts/", (request, response) -> {
            String von = request.queryParams("von") != null ? request.queryParams("von") : "";
            String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";
            String person = request.queryParams("personInput") != null ? request.queryParams("personInput") : "";

            JSONObject newDBData = new JSONObject();
            List<JSONObject> tokenData = mongoDBHandler.getTokenCount(30, von, bis, "", person);
            newDBData.put("token", tokenData);

            List<JSONObject> posData = mongoDBHandler.getPOSCount(von, bis, "", person);
            newDBData.put("pos", posData);

            JSONObject entityData = mongoDBHandler.getNamedEntityCount(von, bis, "", person);
            newDBData.put("entities", entityData);

            // The Updates for the other charts could be added here
            response.type("application/json");
            return newDBData;

        });
    }

    /*
     * GET routes
     */

    /** Test page. */
    @Testing
    private static final TemplateViewRoute getTest = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();
        JSONObject obj = new JSONObject("{\"perEntity\":[{\"_id\":2,\"name\":\"butter\"},{\"_id\":5,\"name\":\"butter\"}]}");
        ArrayList<JSONObject> objList = new ArrayList<>(0);
        objList.add(new JSONObject("{\"_id\":2,\"name\":\"butter\"}"));
        objList.add(new JSONObject("{\"_id\":5,\"name\":\"butter\"}"));

        pageContent.put("title", "butter");
        pageContent.put("obj", obj);
        pageContent.put("objList", objList);

        return new ModelAndView(pageContent, "test.ftl");
    };

    /** Homepage. */
    private static final TemplateViewRoute getHome = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        pageContent.put("title", "Homepage");

        return new ModelAndView(pageContent, "home.ftl");
    };

    /** ADD SHORT DESCRIPTION */
    @Testing
    private static final TemplateViewRoute getMulti = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

       // JSONObject entitiesObject = mongoDBHandler.facetNamedEntities("", "");
       // pageContent.put("entities", entitiesObject);

        List<JSONObject> posAndCounts = mongoDBHandler.getPOSCount("", "", "", "");
        pageContent.put("pos", posAndCounts);

        List<JSONObject> tokenAndCounts = mongoDBHandler.getTokenCount(30, "", "", "", "");
        pageContent.put("token", tokenAndCounts);

        JSONObject datesAndNamedEntities = mongoDBHandler.getNamedEntityCount("","", "","");
        pageContent.put("entities", datesAndNamedEntities);



        return new ModelAndView(pageContent, "multi.ftl");
    };

//    private static final TemplateViewRoute getDataUpdate = (Request request, Response response) -> {
//        //.....
//    }

    /** ADD SHORT DESCRIPTION */
    @Testing
    private static final TemplateViewRoute getDashboard = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        List<JSONObject> posAndCounts = mongoDBHandler.getPOSCount("","", "", "");
        pageContent.put("pos", posAndCounts);

        List<JSONObject> tokenAndCounts = mongoDBHandler.getTokenCount(30,"", "", "", "");
        pageContent.put("token", tokenAndCounts);

        JSONObject datesAndNamedEntities = mongoDBHandler.getNamedEntityCount("","","", "");
        pageContent.put("entities", datesAndNamedEntities);

        return new ModelAndView(pageContent, "dashboard.ftl");
    };

    /** Speech visualisation page. */
    private static final TemplateViewRoute getReden = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject protocolAgendaData = mongoDBHandler.getProtocalAgendaData();

        pageContent.put("protocolAgendaData", protocolAgendaData);

        return new ModelAndView(pageContent, "speechVis.ftl");
    };

    /** Returns a JSON containing all data for a specific speech. */
    private final static Route getSpeechVis = (Request request, Response response) -> {

        String speechID = request.queryParams("speechID") != null ? request.queryParams("speechID") : "";

        return mongoDBHandler.allSpeechData(speechID);
    };

    /** Returns a JSON containing all speech IDs matching the search. */
    private final static Route getSpeechIDs = (Request request, Response response) -> {

        String text = request.queryParams("text") != null ? request.queryParams("text") : "";

        return mongoDBHandler.findSpeech(text);
    };


    @Unfinished("Not working currently. Attempted to test this in  the multi.ftl")
    private final static Route getChartUpdatesAjax = (Request request, Response response) ->{
        // The Datefilters that are gotten through the calendar fields
        String dateFilterOne = request.queryParams("von") != null ? request.queryParams("von") : "";
        String dateFilterTwo = request.queryParams("bis") != null ? request.queryParams("bis") : "";
        // The Redner person gotten through the search field
        String personFilter = request.queryParams("personFilter") != null ? request.queryParams("personFilter") : "";
        /*
        Add the party and fraction filters here that are input through the dropdown menus. Not sure how to do that yet
         */
        JSONObject newDBData = new JSONObject();
        List<JSONObject> tokenData = mongoDBHandler.getTokenCount(30, dateFilterOne, dateFilterTwo, "", personFilter);
        newDBData.put("token", tokenData);

        List<JSONObject> posData = mongoDBHandler.getPOSCount(dateFilterOne, dateFilterTwo, "", personFilter);
        newDBData.put("pos", posData);

        JSONObject entityData = mongoDBHandler.getNamedEntityCount(dateFilterOne, dateFilterTwo, "", personFilter);
        newDBData.put("entities", entityData);

        // The Updates for the other charts could be added here
        response.type("application/json");
        return newDBData;
    };

    /*
     * MISC:
     */

    /**
     * Opens <a href="http://localhost:4567/">http://localhost:4567/</a> in the system's default browser.
     * @throws IOException if an I/O error occurs during execution
     * @author Eric Lakhter
     */
    public static void openInDefaultBrowser() throws IOException {
        openInDefaultBrowser("http://localhost:4567/");
    }

    /**
     * Opens the given URL in the system's default browser.
     * @param url Page to open
     * @throws IOException if an I/O error occurs during execution
     * @author Eric Lakhter
     */
    public static void openInDefaultBrowser(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();

        if (os.contains("win")) {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else if (os.contains("mac")) {
            rt.exec("open " + url);
        } else if (os.contains("nix") || os.contains("nux")) {
            rt.exec("xdg-open " + url);
        }
    }
}
