package utility;

import data.tex.Speech_TeX;
import exceptions.EditorFormattingException;
import exceptions.WrongInputException;
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
 * Starts the localhost server for the protocol visualisation.
 *
 * @author Eric Lakhter
 */
@Testing
@Unfinished("Only some routes are finished")
public class SparkHandler {
    private final static MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();
    private static EditorProtocolParser epParser;

    private static final Configuration cfg = Configuration.getDefaultConfiguration();
    // the added string redirects to the /resources/ directory
    private static final String frontendPath = /* SparkHandler.class.getClassLoader().getResource(".").getPath() + "../../" + */ "src/main/resources/frontend/";

    public static void main(String[] args) throws IOException, UIMAException {
        SparkHandler.init();
//        openInDefaultBrowser();
    }

    private SparkHandler() {}

    /**
     * Sets up the website's paths.
     *
     * @author Eric Lakhter
     * @see #getIcon
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
     * @see #getLoginSite
     */
    public static void init() throws UIMAException, IOException {
        epParser = new EditorProtocolParser(new UIMAPerformer());

        staticFiles.externalLocation(frontendPath + "pdfOutput/");
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
        get("/update-charts/", getChartUpdates);

        get("/reden/", getReden, new FreeMarkerEngine(cfg));
        get("/reden/speechVis/", "application/json", getSpeechVis);
        get("/reden/speechIDs/", "application/json", getSpeechIDs);

        get("/protokolleditor/", getProtokollEditor, new FreeMarkerEngine(cfg));
        post("/protokolleditor/", "application/json", postProtokollEditor);

        get("/latex/", getLaTeX, new FreeMarkerEngine(cfg));
        post("/latex/", "application/json", postLaTeX);

        get("/network/speech/", getSpeechNetwork, new FreeMarkerEngine(cfg));
        get("/network/comment/", getCommentNetwork, new FreeMarkerEngine(cfg));

        get("/loginSite/", getLoginSite, new FreeMarkerEngine(cfg));
        post("/post/applicationDataLogin/", postLogin);
        post("/post/applicationDataRegister/", postRegister);
        post("/post/applicationDataAdminCheck/", postCheckAdmin);
        post("/post/applicationDataManagerCheck/", postCheckManager);
        post("/post/applicationDataUserCheck/", postCheckUser);
        post("/post/applicationDataLogoutUser/", postLogout);
        post("/post/applicationDataDeleteUser/", postDeleteUser);
        post("/post/applicationDataPwChange/", postChangePassword);
        post("/post/applicationDataEditUser/", postEditUser);
    }

    /*
     * Routes:
     */

    /**
     * Website's favicon.
     *
     * @author Eric Lakhter
     */
    private static final Route getIcon = (Request request, Response response) -> {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(ImageIO.read(new File(frontendPath + "favicon.png")), "png", baos);
            return baos.toByteArray();
        }
    };

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

        String dbInfo = "admin";
        pageContent.put("userRank", dbInfo);
        return new ModelAndView(pageContent, "test.ftl");
    };

    /**
     * Homepage.
     *
     * @author Eric Lakhter
     */
    private static final TemplateViewRoute getHome = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        pageContent.put("title", "Homepage");

        return new ModelAndView(pageContent, "home.ftl");
    };

    /**
     * LaTeX editing page.
     * @author
     */
    @Unfinished("Doesn't do anything yet")
    private static final TemplateViewRoute getLaTeX = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        return new ModelAndView(pageContent, "LaTeXEditor.ftl");
    };

    /**
     * Tries to return a PDF file.
     * @author
     */
    @Unfinished("Need to create TeX based on input to compile to a new pdf")
    private static final Route postLaTeX = (Request request, Response response) -> {
        System.out.println("POST postLaTeX aufgerufen");

        System.out.println(request.body()); // this will be the LaTeX text field

        String speechTexString = Speech_TeX.toTeX("ID19100100", true, true, true);

        String successStatus = "PDF successfully generated";
        String successMessage = "/pdfOutput/Abschluss.pdf";

        return successJSON(successStatus, successMessage);
    };

    /**
     * Speech editing page.
     *
     * @author Eric Lakhter
     */
    @Unfinished("Nothing more than a text field so far")
    private static final TemplateViewRoute getProtokollEditor = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        return new ModelAndView(pageContent, "ProtokollEditor.ftl");
    };

    /**
     * Tries to parse a custom protocol/agenda item/speech and to insert it into the DB.
     *
     * @author Eric Lakhter
     */
    @Unfinished("Need to implement the part which grabs info from the db first before this is considered done")
    private static final Route postProtokollEditor = (Request request, Response response) -> {
        try {
            String editMode = request.queryParams("editMode");
            if (editMode == null)
                throw new EditorFormattingException("editMode must be either \"protocol\", \"aItem\" or \"speech\" but is null");

            boolean allowOverwrite = Objects.equals(request.queryParams("overwrite"), "true");
            // Testing
            System.out.println("allowOverwrite is: " + allowOverwrite);
            allowOverwrite = false;

            String id;
            String successStatus;
            switch (editMode) {
                case "protocol":
                    id = epParser.parseEditorProtocol(request.body(), allowOverwrite);
                    successStatus = "Protocol \"" + id + "\" successfully inserted";
                    break;
                case "aItem":
                    id = epParser.parseEditorAgendaItem(request.body(), allowOverwrite);
                    successStatus = "AgendaItem \"" + id + "\"  successfully inserted";
                    break;
                case "speech":
                    id = epParser.parseEditorSpeech(request.body(), allowOverwrite);
                    successStatus = "Speech \"" + id + "\"  successfully inserted";
                    break;
                default:
                    throw new EditorFormattingException("editMode must be either \"protocol\", \"aItem\" or \"speech\" but is " + editMode);
            }
            return successJSON(successStatus, "null");
        } catch (EditorFormattingException | WrongInputException e) {
            return errorJSON(e.getMessage());
        } catch (Exception e) {
            return errorJSON("General Exception: " + e.getMessage());
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

        JSONObject protocolAgendaData = mongoDBHandler.getProtocalAgendaData();

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

    private static final TemplateViewRoute getSpeechNetwork = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject networkData = mongoDBHandler.matchSpeakerToDDC();

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

    /**
     * This returns the login page.
     *
     * @author Julian Ocker
     */
    private static final TemplateViewRoute getLoginSite = (request, response) -> {
        Map<String, Object> pageContent = new HashMap<>(0);
        String cookie = request.cookie("key");
        if (mongoDBHandler.checkUser(cookie) || mongoDBHandler.checkManager(cookie)) {
            pageContent.put("loginStatus", true);
        } else {
            pageContent.put("loginStatus", false);
        }
        if (mongoDBHandler.checkAdmin(cookie)) {
            pageContent.put("adminStatus", true);
            pageContent.put("loginStatus", true);
            ArrayList<User> userList = new ArrayList<>(0);
            mongoDBHandler.getDB().getCollection("user").find().forEach(
                    (Consumer<? super Document>) procBlock -> userList.add(new User(procBlock)));
            pageContent.put("userList", userList);
        } else {
            pageContent.put("adminStatus", false);
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
        System.out.println(mongoDBHandler.checkAdmin(cookie));
        if (mongoDBHandler.checkAdmin(cookie) && !deleteUser.equals("Admin1")) {
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
     * accepts cookie, returns whether a user ist registered
     *
     * @author Julian Ocker
     */
    private static final Route postCheckUser = (request, response) -> {
        JSONObject req = new JSONObject(request.body());
        String cookie = req.getString("cookie");
        JSONObject answer = new JSONObject();
        answer.put("answer", mongoDBHandler.checkUser(cookie));
        return answer;
    };

    /**
     * accepts cookie returns whether a User is a Manager
     *
     * @author Julian Ocker
     */
    private static final Route postCheckManager = (request, response) -> {
        JSONObject req = new JSONObject(request.body());
        String cookie = req.getString("cookie");
        JSONObject answer = new JSONObject();
        answer.put("answer", mongoDBHandler.checkManager(cookie));
        return answer;
    };

    /**
     * accepts cookie returns whether a User is an Admin
     *
     * @author Julian Ocker
     */
    private static final Route postCheckAdmin = (request, response) -> {
        JSONObject req = new JSONObject(request.body());
        String cookie = req.getString("cookie");
        JSONObject answer = new JSONObject();
        answer.put("answer", mongoDBHandler.checkAdmin(cookie));
        return answer;
    };

    /**
     * accepts cookie name password rank
     *
     * @returns
     * @author Julian Ocker
     */
    private static final Route postRegister = (request, response) -> {
        System.out.println(request.body());
        JSONObject req = new JSONObject(request.body());
        String name = req.getString("name");
        String password = req.getString("pw");
        String rank = req.getString("rank");
        boolean registrationSuccess = false;

        if (mongoDBHandler.checkIfAvailable(name)) {
            registrationSuccess = mongoDBHandler.registrate(name, password, rank);
        }
        return new JSONObject().put("registration", registrationSuccess);
    };

    /**
     * accepts name and pw returns cookie
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
        if(oldID.equals("Admin1")){
            newRank = "admin";
            newID = "Admin1";
        }
        if (mongoDBHandler.checkAdmin(req.getString("cookie"))) {
            if (mongoDBHandler.editUser(oldID, newID, newPassword, newRank)) {
                answer.put("EditSuccess", true);
            }
        }
        answer.put("EditSuccess", false);
        return answer;
    };

    /*
     * MISC:
     */

    /**
     * Returns a JSON signaling that the request was handled without errors.
     *
     * @return JSON with successMessage
     * @author Eric Lakhter
     */
    private static JSONObject successJSON(String successStatus, String successMessage) {
        if (successStatus == null) successStatus = "null";
        if (successMessage == null) successMessage = "null";
        return new JSONObject().put("status", successStatus).put("message", successMessage);
    }

    /**
     * Returns a JSON signaling that an error occurred while handling the request.
     *
     * @return JSON with errorMessage
     * @author Eric Lakhter
     */
    private static JSONObject errorJSON(String errorMessage) {
        if (errorMessage == null) errorMessage = "null";
        return new JSONObject().put("status", "Error").put("message", errorMessage);
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
