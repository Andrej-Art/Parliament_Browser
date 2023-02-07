package utility;

import exceptions.EditorFormattingException;
import exceptions.WrongInputException;
import freemarker.template.Configuration;
import org.json.JSONObject;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import utility.annotations.*;
import utility.webservice.EditorProtocolParser;

import javax.imageio.ImageIO;
import java.io.*;
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
@Unfinished("Only some routes are finished")
public class SparkHandler {
    private static MongoDBHandler mongoDBHandler = null;
    private static EditorProtocolParser epParser = null;
    private static final Configuration cfg = Configuration.getDefaultConfiguration();
    // the added string redirects to the /resources/ directory
    private static final String frontendPath = /* SparkHandler.class.getClassLoader().getResource(".").getPath() + "../../" + */ "src/main/resources/frontend/";

    public static void main(String[] args) throws IOException {
        MongoDBHandler mdbh = new MongoDBHandler();
        EditorProtocolParser editorProtocolParser = new EditorProtocolParser(mdbh);
        SparkHandler.init(mdbh, editorProtocolParser);
//        openInDefaultBrowser();
    }

    /**
     * Sets up the website's paths.
     * @see #getHome
     * @author Eric Lakhter
     */
    public static void init(MongoDBHandler mdbh, EditorProtocolParser editorProtocolParser) throws IOException {
        epParser = editorProtocolParser;
        mongoDBHandler = mdbh;
//        staticFiles.externalLocation(frontendPath);
        cfg.setDirectoryForTemplateLoading(new File(frontendPath));
        cfg.setDefaultEncoding("UTF-8");
        port(4567);
        // If there aren't any query params redirect the user to a path with trailing slash
        before((req, res) -> {
            String path = req.pathInfo();
            if (!path.endsWith("/") && req.queryParams().size() == 0)
                res.redirect(path + "/") ;
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

        get("/favicon.ico/", "image/png", getIcon);

        get("/", getHome, new FreeMarkerEngine(cfg));

        get("/dashboard/", getDashboard, new FreeMarkerEngine(cfg));
        //Route to deliver the updated Data for the charts according to the provided filters//
        get("/update-charts/", getChartUpdates);

        get("/reden/", getReden, new FreeMarkerEngine(cfg));
        get("/reden/speechVis/", "application/json", getSpeechVis);
        get("/reden/speechIDs/", "application/json", getSpeechIDs);

        get("/latex/", getLaTeX, new FreeMarkerEngine(cfg));
        post("/latex/post/", "application/json", postLaTeX);

        get("/protokolleditor/", getProtokollEditor, new FreeMarkerEngine(cfg));
        post("/protokolleditor/post/", "application/json", postProtokollEditor);

        get("/network/1/", getNetwork, new FreeMarkerEngine(cfg));
        get("/network/2/", getNetwork2, new FreeMarkerEngine(cfg));
    }

    /*
     * Routes:
     */

    /** Website's favicon. */
    private static final Route getIcon = (Request request, Response response) -> {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(ImageIO.read(new File(frontendPath + "favicon.png")),"png" , baos);
            return baos.toByteArray();
        }
    };

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

    /** LaTeX editing page. */
    @Unfinished("Doesn't do anything yet")
    private static final TemplateViewRoute getLaTeX = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

//        JSONObject commands = mongoDBHandler.getLaTeXCommands();

//        pageContent.put("commands", commands);

        return new ModelAndView(pageContent, "LaTeXEditor.ftl");
    };

    /** Tries to return a PDF file. */
    @Unfinished("Need to convert the LaTeX code to a pdf")
    private static final Route postLaTeX = (Request request, Response response) -> {
        System.out.println("POST postLaTeX aufgerufen");

        System.out.println(request.body()); // this will be the LaTeX text field

        String successMessage = "null";

        return successJson(successMessage);
    };

    /** Speech editing page. */
    @Unfinished("Nothing more than a text field so far")
    private static final TemplateViewRoute getProtokollEditor = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        return new ModelAndView(pageContent, "ProtokollEditor.ftl");
    };

    /** Tries to parse a custom protocol/agenda item/speech and to insert it into the DB. */
    @Unfinished("Need to turn the speech into a database object")
    private static final Route postProtokollEditor = (Request request, Response response) -> {
        System.out.println("POST postProtokollEditor aufgerufen");
        System.out.println(request.body()); // this will be what's going to be parsed into a protocol/agenda item/speech

        try {
            if (request.queryParams("editMode") == null)
                throw new EditorFormattingException("editMode must be either \"protocol\", \"aItem\" or \"speech\" but is null");
            String editMode = request.queryParams("editMode");
            String successMessage;
            switch (editMode) {
                case "protocol":
                    epParser.parseEditorProtocol(request.body());
                    successMessage = "Protocol successfully inserted";
                    break;
                case "aItem":
                    epParser.parseEditorAgendaItem(request.body());
                    successMessage = "AgendaItem successfully inserted";
                    break;
                case "speech":
                    epParser.parseEditorSpeech(request.body());
                    successMessage = "Speech successfully inserted";
                    break;
                default:
                    throw new EditorFormattingException("editMode must be either \"protocol\", \"aItem\" or \"speech\" but is " + editMode);
            }
            return successJson(successMessage);
        } catch (EditorFormattingException | WrongInputException e) {
            return errorJson(e.getMessage());
        }
    };

//    private static final TemplateViewRoute getDataUpdate = (Request request, Response response) -> {
//        //.....
//    }

    /** ADD SHORT DESCRIPTION */
    @Testing
    private static final TemplateViewRoute getDashboard = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        List<JSONObject> posAndCounts = mongoDBHandler.getPOSCount("", "","", "", "");
        pageContent.put("pos", posAndCounts);

        List<JSONObject> tokenAndCounts = mongoDBHandler.getTokenCount(30,"", "","", "", "");
        pageContent.put("token", tokenAndCounts);

        JSONObject datesAndNamedEntities = mongoDBHandler.getNamedEntityCount("", "","","", "");
        pageContent.put("entities", datesAndNamedEntities);

        List<JSONObject> speechesCounts = mongoDBHandler.getSpeechesBySpeakerCount("", "", "", "", "", 1200);
        pageContent.put("speechesNumber", speechesCounts);

        //JSONObject sentiments = mongoDBHandler.getSentimentData("", "", "", "");
        //pageContent.put("sentiments", sentiments);

        List<JSONObject> votes = mongoDBHandler.getPollResults("", "", "", "", "");
        pageContent.put("votes", votes);

        return new ModelAndView(pageContent, "dashboard.ftl");
    };

    private static final Route getChartUpdates = (Request request, Response response) -> {
        String von = request.queryParams("von") != null ? request.queryParams("von") : "";
        String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";
        String person = request.queryParams("personInput") != null ? request.queryParams("personInput") : "";
        String fraction = request.queryParams("fraction") != null ? request.queryParams("fraction") : "";
        String party = request.queryParams("party") != null ? request.queryParams("party") : "";

        JSONObject newDBData = new JSONObject();
        List<JSONObject> tokenData = mongoDBHandler.getTokenCount(30, von, bis, fraction, party, person);
        newDBData.put("token", tokenData);

        List<JSONObject> posData = mongoDBHandler.getPOSCount(von, bis, fraction, party, person);
        newDBData.put("pos", posData);

        JSONObject entityData = mongoDBHandler.getNamedEntityCount(von, bis, fraction, party, person);
        newDBData.put("entities", entityData);

        List<JSONObject> speechesCountData = mongoDBHandler.getSpeechesBySpeakerCount(von, bis, fraction, party, person, 1200);
        newDBData.put("speechesNumber", speechesCountData);

        //JSONObject sentimentData = mongoDBHandler.getSentimentData(von, bis, "", person);
        //newDBData.put("sentiment", sentimentData);

        List<JSONObject> votes = mongoDBHandler.getPollResults(von, bis, fraction, party, person);
        newDBData.put("votes", votes);

        // The Updates for the other charts could be added here
        response.type("application/json");
        return newDBData;
    };

    /** Speech visualisation page. */
    private static final TemplateViewRoute getReden = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject protocolAgendaData = mongoDBHandler.getProtocalAgendaData();

        pageContent.put("protocolAgendaData", protocolAgendaData);

        return new ModelAndView(pageContent, "speechVis.ftl");
    };

    /** Returns a JSON containing all data for a specific speech. */
    private static final Route getSpeechVis = (Request request, Response response) -> {

        String speechID = request.queryParams("speechID") != null ? request.queryParams("speechID") : "";

        return mongoDBHandler.allSpeechData(speechID);
    };

    /** Returns a JSON containing all speech IDs matching the search. */
    private static final Route getSpeechIDs = (Request request, Response response) -> {

        String text = request.queryParams("text") != null ? request.queryParams("text") : "";

        return mongoDBHandler.findSpeech(text);
    };

    private final static TemplateViewRoute getNetwork = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject networkData = mongoDBHandler.matchSpeakerToDDC();

        pageContent.put("redeNetworkData", networkData);

        return new ModelAndView(pageContent, "speechNetwork.ftl");
    };
    private final static TemplateViewRoute getNetwork2 = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject networkData = mongoDBHandler.commentatorToSpeaker();

        pageContent.put("commentNetworkData", networkData);

        return new ModelAndView(pageContent, "commentNetwork.ftl");
    };


    /*
     * MISC:
     */

    /**
     * Returns a JSON signaling that the request was handled without errors.
     * @return JSON with successMessage
     * @author Eric Lakhter
     */
    private static String successJson(String successMessage) {
        return "{\"status\":\"Success\",\"message\":\"" + successMessage.replace("\"", "\\\"") + "\"}";
    }

    /**
     * Returns a JSON signaling that an error occurred while handling the request.
     * @return JSON with errorMessage
     * @author Eric Lakhter
     */
    private static String errorJson(String errorMessage){
        return "{\"status\":\"Error\",\"message\":\"" + errorMessage.replace("\"", "\\\"") + "\"}";
    }

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
