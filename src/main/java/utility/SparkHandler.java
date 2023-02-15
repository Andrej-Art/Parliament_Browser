package utility;

import data.tex.GoodWindowsExec;
import data.tex.LaTeXHandler;
import exceptions.EditorException;
import freemarker.template.Configuration;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.json.JSONObject;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import utility.annotations.*;
import utility.webservice.EditorProtocolParser;
import utility.webservice.User;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import static spark.Spark.*;

/**
 * Starts the localhost server for the protocol visualisation.<br>
 * Contains all Routes and their definitions.
 *
 * @author Eric Lakhter
 * @author Edvin Nise
 * @author Julian Ocker
 * @author Andrej Artuschenko
 * @author DavidJordan
 */
@Testing
@Unfinished("Only some routes are finished")
public class SparkHandler {
    private final static MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();
    private static EditorProtocolParser epParser;
    private static final Configuration cfg = Configuration.getDefaultConfiguration();
    private static final String frontendPath = /* SparkHandler.class.getClassLoader().getResource(".").getPath() + "../../" + */ "src/main/resources/frontend/";

    public static void main(String[] args) throws IOException, UIMAException {
        SparkHandler.init();
//        openInDefaultBrowser();
    }

    private SparkHandler() {
    }

    /**
     * Sets up the website's paths.
     *
     * @author Eric Lakhter
     * @author Edvin Nise
     * @author Julian Ocker
     * @author Andrej Artuschenko
     * @author DavidJordan
     * @see #getHome
     * @see #getDashboard
     * @see #getChartUpdates
     * @see #getReden
     * @see #getSpeechVis
     * @see #getSpeechIDs
     * @see #getProtokollEditor
     * @see #getLaTeX
     * @see #getSpeechNetwork
     * @see #getCommentNetwork
     * @see #getSpeechTopicNetwork
     * @see #getLoginSite
     */
    public static void init() throws UIMAException, IOException {
        epParser = new EditorProtocolParser();

        staticFiles.externalLocation(frontendPath + "public/");
        cfg.setDirectoryForTemplateLoading(new File(frontendPath));
        cfg.setDefaultEncoding("UTF-8");
        port(4567);
        // If there aren't any query params redirect the user to a path with trailing slash
        before((req, res) -> {
            String path = req.pathInfo();
            if (!path.endsWith("/") && req.queryParams().size() == 0)
                res.redirect(path + "/");
        });

        // Test is for testing
        get("/test/", getTest, new FreeMarkerEngine(cfg));

        get("/", getHome, new FreeMarkerEngine(cfg));
        post("/", "application/json", postHome);

        get("/dashboard/", getDashboard, new FreeMarkerEngine(cfg));
        get("/update-charts/", getChartUpdates);

        get("/reden/", getReden, new FreeMarkerEngine(cfg));
        get("/reden/speechVis/", "application/json", getSpeechVis);
        get("/reden/speechIDs/", "application/json", getSpeechIDs);

        get("/protokolleditor/", getProtokollEditor, new FreeMarkerEngine(cfg));
        post("/protokolleditor/insert/", "application/json", postProtokollEditorInsert);
        post("/protokolleditor/extract/", "application/json", postProtokollEditorExtract);

        get("/latex/", getLaTeX, new FreeMarkerEngine(cfg));
        get("/latex/protocol/", "application/json", getLaTeXString);
        post("/latex/", "application/json", postLaTeX);

        get("/network/speech/", getSpeechNetwork, new FreeMarkerEngine(cfg));
        get("/network/comment/", getCommentNetwork, new FreeMarkerEngine(cfg));
        get("/network/topic/", getSpeechTopicNetwork, new FreeMarkerEngine(cfg));

        get("/loginSite/", getLoginSite, new FreeMarkerEngine(cfg));
        post("/post/applicationDataLogin/", postLogin);
        post("/post/applicationDataRegister/", postRegister);
        post("/post/applicationDataLogoutUser/", postLogout);
        post("/post/applicationDataDeleteUser/", postDeleteUser);
        post("/post/applicationDataPwChange/", postChangePassword);
        post("/post/applicationDataEditUser/", postEditUser);
    }

    /*
     * Routes:
     */

    /**
     * Test page.
     */
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

        String userRank = "admin";
        pageContent.put("userRank", userRank);
        return new ModelAndView(pageContent, "test.ftl");
    };

    /**
     * Homepage.
     *
     * @author Eric Lakhter
     */
    private static final TemplateViewRoute getHome = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        pageContent.put("isHomepage", true);

        return new ModelAndView(pageContent, "home.ftl");
    };

    /**
     * DB availability check.
     *
     * @author Eric Lakhter
     */
    private static final Route postHome = (Request request, Response response) -> {
        try {
            mongoDBHandler.getDocumentOrNull("protocol", "19/1");
            return responseJSON("Success", "Datenbank ist online und verfügbar!");
        } catch (Exception e) {
            return responseJSON("Error", "Datenbankverbindung ist zurzeit nicht verfügbar.");
        }
    };

    /**
     * LaTeX editing page.
     *
     * @author DavidJordan
     */
    @Unfinished("Attempts to get all Protocol data, inclding the IDs which are supposed to be " +
            "inserted into the button labels")
    private static final TemplateViewRoute getLaTeX = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();
        pageContent.put("protocolData", mongoDBHandler.getProtocolAgendaPersonData());

        return new ModelAndView(pageContent, "LaTeXEditor.ftl");
    };

    /**
     * Delivers the required String in LaTeX format to the frontend.
     * @author DavidJordan
     */
    @Unfinished("Works, but not finished")
    private static final Route getLaTeXString = (Request request, Response response) -> {
        JSONObject data = new JSONObject();
        String protocolID = request.queryParams("protocolID") != null ? request.queryParams("protocolID") : "";
        String originalprotocolTex = "";
        try {
            LaTeXHandler laTeXHandler = new LaTeXHandler(mongoDBHandler, "src/main/resources/frontend/public/pdfOutput");
            originalprotocolTex = laTeXHandler.createTEX(protocolID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        data.append("latexString", originalprotocolTex);
        return data;
    };

    /**
     * Route that returns a .pdf file from the Latex String
     *
     * @author DavidJordan
     */
    @Unfinished("Need to create TeX based on input to compile to a new pdf")
    private static final Route postLaTeX = (Request request, Response response) -> {
        System.out.println("POST postLaTeX aufgerufen");

        System.out.println(request.body()); // this will be the LaTeX text field

        LaTeXHandler texHandler = new LaTeXHandler(mongoDBHandler, "src/main/resources/frontend/public/pdfOutput/");
        String editedLatexString =  request.body();



        texHandler.createPDF(editedLatexString);
        GoodWindowsExec.main(new String[]{"pdflatex.exe -shell-escape  -output-directory=src\\main\\resources\\frontend\\public\\pdfOutput protocol.tex"});


        String successStatus = "PDF successfully generated";
        String successMessage = "/pdfOutput/protocol.pdf";
        JSONObject pdfURL = new JSONObject();
        pdfURL.put("status", successStatus);
        pdfURL.put("message", successMessage);

        return pdfURL;
    };

    /**
     * Speech editing page.
     *
     * @author Eric Lakhter
     */
    @Unfinished("Nothing more than a text field so far")
    private static final TemplateViewRoute getProtokollEditor = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();
        // something user-rank related
        pageContent.put("protocolAgendaPersonData", mongoDBHandler.getProtocolAgendaPersonData());
        return new ModelAndView(pageContent, "protocolEditor.ftl");
    };

    /**
     * Tries to parse a custom protocol/agenda item/speech and to insert it into the DB.
     *
     * @author Eric Lakhter
     */
    @Unfinished("Need to implement the part which grabs info from the db first before this is considered done")
    private static final Route postProtokollEditorInsert = (Request request, Response response) -> {
        try {
            String editMode = request.queryParams("editMode");
            if (editMode == null)
                throw new EditorException("editMode must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is null");

            boolean allowOverwrite = Objects.equals(request.queryParams("overwrite"), "true");
            System.out.println("allowOverwrite is: " + allowOverwrite);

            String id;
            String successStatus;
            switch (editMode) {
                case "protocol":
                    id = epParser.parseEditorProtocol(new JSONObject(request.body()), allowOverwrite);
                    successStatus = "Protokoll \"" + id + "\" erfolgreich in die Datenbank eingefügt";
                    break;
                case "aItem":
                    id = epParser.parseEditorAgendaItem(new JSONObject(request.body()), allowOverwrite);
                    successStatus = "Tagesordnungspunkt \"" + id + "\"  erfolgreich in die Datenbank eingefügt";
                    break;
                case "speech":
                    id = epParser.parseEditorSpeech(new JSONObject(request.body()), allowOverwrite);
                    successStatus = "Rede \"" + id + "\"  erfolgreich in die Datenbank eingefügt";
                    break;
                case "person":
                    id = epParser.parseEditorPerson(new JSONObject(request.body()), allowOverwrite);
                    successStatus = "Person \"" + id + "\"  erfolgreich in die Datenbank eingefügt";
                    break;
                default:
                    throw new EditorException("editMode must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is " + editMode);
            }
            return responseJSON(successStatus, "null");
        } catch (Exception e) {
            return responseJSON("Error", e.getMessage());
        }
    };

    /**
     * Tries to parse a custom protocol/agenda item/speech and to insert it into the DB.
     *
     * @author Eric Lakhter
     */
    @Unfinished("Need to implement person extractor and to put it on the webpage")
    private static final Route postProtokollEditorExtract = (Request request, Response response) -> {
        try {
            String col = request.queryParams("col");
            if (col == null)
                throw new EditorException("col must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is null");
            String id = request.queryParams("id");
            if (id == null)
                throw new EditorException("id is null");
            switch (col) {
                case "protocol":
                    return responseJSON("Protokoll " + id + " erfolgreich geladen", epParser.getEditorProtocolFromDB(id));
                case "aItem":
                    return responseJSON("Tagesordnungspunkt " + id + " erfolgreich geladen", epParser.getEditorAgendaFromDB(id));
                case "speech":
                    return responseJSON("Rede " + id + " erfolgreich geladen", epParser.getEditorSpeechFromDB(id));
                case "person":
                    return responseJSON("Person " + id + " erfolgreich geladen", epParser.getEditorPersonFromDB(id));
                default:
                    throw new EditorException("Collection must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is " + col);
            }
        } catch (Exception e) {
            return responseJSON("Error", e.getMessage());
        }
    };

    private static final TemplateViewRoute getDashboard = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

//        List<JSONObject> posAndCounts = mongoDBHandler.getPOSCount("", "","", "", "");
//        pageContent.put("pos", posAndCounts);
//
//        List<JSONObject> tokenAndCounts = mongoDBHandler.getTokenCount(30,"", "","", "", "");
//        pageContent.put("token", tokenAndCounts);
//
//        JSONObject datesAndNamedEntities = mongoDBHandler.getNamedEntityCount("", "","","", "");
//        pageContent.put("entities", datesAndNamedEntities);
//
//        List<JSONObject> speechesCounts = mongoDBHandler.getSpeechesBySpeakerCount("", "", "", "", "", 15);
//        pageContent.put("speechesNumber", speechesCounts);
//
//        //JSONObject sentiments = mongoDBHandler.getSentimentData("", "", "", "");
//        //pageContent.put("sentiments", sentiments);
//
        ArrayList<JSONObject> votes = mongoDBHandler.getPollResults("", "", "", "", "");
//        pageContent.put("votes", votes);

        return new ModelAndView(pageContent, "dashboard.ftl");
    };

    /**
     * Route which delivers the data according to the provided query parameters
     * @author DavidJordan
     */
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

        List<JSONObject> speechesCountData = mongoDBHandler.getSpeechesBySpeakerCount(von, bis, fraction, party, person, 15);
        newDBData.put("speechesNumber", speechesCountData);

        JSONObject sentimentData = mongoDBHandler.getSentimentData(von, bis, "", party, person);
        newDBData.put("sentiment", sentimentData);

        ArrayList<JSONObject> votes = mongoDBHandler.getPollResults(von, bis, fraction, party, person);
        newDBData.put("votes", votes);

        // The Updates for the other charts could be added here
        response.type("application/json");
        return newDBData;
    };

    /**
     * Speech visualisation page.
     *
     * @author Eric Lakhter
     */
    private static final TemplateViewRoute getReden = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject protocolAgendaData = mongoDBHandler.getProtocolAgendaPersonData();

        pageContent.put("protocolAgendaData", protocolAgendaData);

        return new ModelAndView(pageContent, "speechVis.ftl");
    };

    /**
     * Returns a JSON containing all data for a specific speech.
     *
     * @author Eric Lakhter
     */
    private static final Route getSpeechVis = (Request request, Response response) -> {
        String speechID = request.queryParams("speechID") != null ? request.queryParams("speechID") : "";
        return mongoDBHandler.allSpeechData(speechID);
    };

    /**
     * Returns a JSON containing all speech IDs matching the search.
     *
     * @author Eric Lakhter
     */
    private static final Route getSpeechIDs = (Request request, Response response) -> {
        String text = request.queryParams("text") != null ? request.queryParams("text") : "";
        return mongoDBHandler.findSpeech(text);
    };
//    GoodWindowsExec.main(new String[]{"pdflatex.exe -shell-escape  -output-directory C:\\Users\\edvin\\IdeaProjects\\Übung5\\src\\main\\resources\\frontend\\public\\pdfOutput testPDF3.tex"});

    private static final TemplateViewRoute getSpeechNetwork = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();
        String von = request.queryParams("von") != null ? request.queryParams("von") : "";
        String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";

        JSONObject networkData = mongoDBHandler.matchSpeakerToDDC(von, bis);

        pageContent.put("redeNetworkData", networkData);

        return new ModelAndView(pageContent, "speechNetwork.ftl");
    };
    private static final TemplateViewRoute getCommentNetwork = (Request request, Response response) -> {
        String von = request.queryParams("von") != null ? request.queryParams("von") : "";
        String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject networkData = mongoDBHandler.commentatorToSpeaker(von, bis);

        pageContent.put("commentNetworkData", networkData);

        return new ModelAndView(pageContent, "commentNetwork.ftl");
    };

    private static final TemplateViewRoute getSpeechTopicNetwork = (Request request, Response response) -> {
        String von = request.queryParams("von") != null ? request.queryParams("von") : "";
        String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject networkData = mongoDBHandler.speechSentTopicData(von, bis);

        pageContent.put("speechTopicNetworkData", networkData);

        return new ModelAndView(pageContent, "speechTopicNetwork.ftl");
    };

    /**
     * This returns the login page.
     *
     * @author Julian Ocker
     */
    private static final TemplateViewRoute getLoginSite = (request, response) -> {
        Map<String, Object> pageContent = new HashMap<>(0);
        String cookie = request.cookie("key");
        if (mongoDBHandler.checkIfCookieExists(cookie)) {
            pageContent.put("loginStatus", true);
            if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editUsers")) {
                pageContent.put("adminStatus", true);
                pageContent.put("loginStatus", true);
                ArrayList<User> userList = new ArrayList<>(0);
                mongoDBHandler.getDB().getCollection("user").find().forEach(
                        (Consumer<? super Document>) procBlock -> userList.add(new User(procBlock)));
                pageContent.put("userList", userList);
            } else {
                pageContent.put("editUserRight", false);
            }
            pageContent.put("addUserRight", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "addUsers"));
            pageContent.put("deleteUserRight", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteUsers"));
        } else {
            pageContent.put("loginStatus", false);
        }

        return new ModelAndView(pageContent, "login.ftl");
    };

    /**
     * accepts cookie oldPw newPw, returns whether the change was successful
     *
     * @author Julian Ocker
     */
    private static final Route postChangePassword = (request, response) -> {
        JSONObject req = new JSONObject(request.body());
        String oldPassword = req.getString("oldPw");
        String newPassword = req.getString("newPw");
        String cookie = req.getString("cookie");
        Boolean success = mongoDBHandler.changePassword(cookie, newPassword, oldPassword);
        mongoDBHandler.logout(cookie);
        return new JSONObject().put("pwChangeSuccess", success);
    };

    /**
     * accepts cookie deleteUser, returns whether the deletion was successful
     *
     * @author Julian Ocker
     */
    private static final Route postDeleteUser = (request, response) -> {
        JSONObject req = new JSONObject(request.body());
        String deleteUser = req.getString("deleteUser");
        String cookie = req.getString("cookie");
        System.out.println(mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteUsers"));
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteUsers")) {
            JSONObject uDeletionSuccess = new JSONObject().put("deletionSuccess", mongoDBHandler.deleteUser(deleteUser));
            return uDeletionSuccess;
        }
        return new JSONObject().put("deletionSuccess", false);
    };

    /**
     * accepts cookie, logs a User out
     *
     * @author Julian Ocker
     */
    private static final Route postLogout = (request, response) -> {
        JSONObject req = new JSONObject(request.body());
        String deleteCookie = req.getString("logoutUser");
        return new JSONObject().put("cDeletionSuccess", mongoDBHandler.logout(deleteCookie));
    };

    /**
     * accepts cookie name password rank.
     *
     * @author Julian Ocker
     */
    private static final Route postRegister = (request, response) -> {
        System.out.println(request.body());
        JSONObject req = new JSONObject(request.body());
        String name = req.getString("name");
        String password = req.getString("pw");
        String rank = req.getString("rank");
        boolean registrationSuccess = false;
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(req.getString("cookie"), "addUsers")) {
            if (mongoDBHandler.checkIfAvailable(name)) {
                registrationSuccess = mongoDBHandler.register(name, password, rank);
            }
        }
        return new JSONObject().put("registration", registrationSuccess);
    };

    /**
     * accepts name and pw returns cookie.
     *
     * @author Julian Ocker
     */
    private static final Route postLogin = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        String name = req.getString("name");
        String password = req.getString("pw");
        JSONObject answer = new JSONObject();
        if (mongoDBHandler.checkUserAndPassword(name, password)) {
            String cookie = mongoDBHandler.generateCookie(name, password);
            answer.put("cookie", cookie);
            answer.put("loginSuccess", true);
        } else {
            answer.put("cookie", "Deine Anmeldedaten sind Falsch!");
            answer.put("loginSuccess", false);
        }
        return answer;
    };

    /**
     * accepts oldID newID newPassword new Rank, returns if Success
     *
     * @author Julian Ocker
     */
    private static final Route postEditUser = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        String oldID = req.getString("editOldID");
        String newID = req.getString("editNewID");
        String newPassword = req.getString("editPassword");
        String newRank = req.getString("editRank");
        JSONObject answer = new JSONObject();
        if (oldID.equals("Admin1")) {
            newRank = "admin";
            newID = "Admin1";
        }
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(req.getString("cookie"), "editUsers")) {
            if (mongoDBHandler.editUser(oldID, newID, newPassword, newRank)) {
                answer.put("EditSuccess", true);
                return answer;
            }
        }
        answer.put("EditSuccess", false);
        return answer;
    };

    /*
     * MISC:
     */

    /**
     * Returns a JSON with a "status" and a "details" attribute.
     * <p>The status attribute should be "Error" if an error occurred, or a success message instead.<br>
     * The details attribute is the content the page handles, e.g. an error message
     * or a String to be inserted somewhere or another JSON.
     *
     * @return JSON: {@code {status: status, details: details}}
     * @author Eric Lakhter
     */
    private static JSONObject responseJSON(String status, Object details) {
        if (status == null) status = "null";
        if (details == null) details = "null";
        return new JSONObject().put("status", status).put("details", details);
    }

    /**
     * Opens <a href="http://localhost:4567/">http://localhost:4567/</a> in the system's default browser.
     *
     * @throws IOException if an I/O error occurs during execution
     * @author Eric Lakhter
     */
    public static void openInDefaultBrowser() throws IOException {
        openInDefaultBrowser("http://localhost:4567/");
    }

    /**
     * Opens the given URL in the system's default browser.
     *
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
