package utility;

import data.tex.CommandLineExec;
import data.tex.LaTeXHandler;
import exceptions.EditorException;
import freemarker.template.Configuration;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.json.JSONObject;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import utility.webservice.EditorProtocolParser;
import utility.webservice.User;

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
public class SparkHandler {
    private final static MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();
    private static EditorProtocolParser epParser;

    private static final Configuration cfg = Configuration.getDefaultConfiguration();
    private static final String frontendPath = "src/main/resources/frontend/";

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
        post("/protokolleditor/delete/", "application/json", postProtokollEditorDelete);

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

        get("/featureManagement/", getFeatureManagement, new FreeMarkerEngine(cfg));
        post("/post/applicationDataEditFeatures/", postEditFeatures);

        get("/ProtocolCheckerLoader/", getProtocolCheckerLoader, new FreeMarkerEngine(cfg));
        post("/post/applicationDataLoadAll/", postLoadAll);
        post("/post/applicationDataParseAll/", postParseAll);
        post("/post/applicationDataParseNew/", postParseNew);
        post("/post/applicationDataParseSingle/", postParseSingle);
    }

    /*
     * Routes:
     */

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
    private static final TemplateViewRoute getLaTeX = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();
        pageContent.put("protocolData", mongoDBHandler.getProtocolAgendaPersonData());
        pageContent.put("canEdit", mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "manager"));

        return new ModelAndView(pageContent, "LaTeXEditor.ftl");
    };

    /**
     * Delivers the required String in LaTeX format to the frontend.
     *
     * @author DavidJordan
     */
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
    private static final Route postLaTeX = (Request request, Response response) -> {
        System.out.println("POST postLaTeX aufgerufen");

        System.out.println(request.body()); // this will be the LaTeX text field

        LaTeXHandler texHandler = new LaTeXHandler(mongoDBHandler, "src/main/resources/frontend/public/pdfOutput/");
        String editedLatexString = request.body();

        // Comand Line that runs the pdflatex command on
        texHandler.createPDF(editedLatexString);
        CommandLineExec.main(new String[]{"pdflatex.exe -shell-escape  -output-directory=src\\main\\resources\\frontend\\public\\pdfOutput protocol.tex"});


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
    private static final TemplateViewRoute getProtokollEditor = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        String cookie = request.cookie("key");

        pageContent.put("permissions", getEditorPermissions(cookie));
        pageContent.put("protocolAgendaPersonData", mongoDBHandler.getProtocolAgendaPersonData());

        return new ModelAndView(pageContent, "protocolEditor.ftl");
    };

    /**
     * Tries to parse a custom protocol/agenda item/speech/person and to insert it into the DB.
     *
     * @author Eric Lakhter
     */
    private static final Route postProtokollEditorInsert = (Request request, Response response) -> {
        try {
            String editMode = request.queryParams("editMode");
            if (editMode == null)
                throw new EditorException("editMode must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is null");

            boolean allowOverwrite = Objects.equals(request.queryParams("overwrite"), "true");

            String id;
            switch (editMode) {
                case "protocol":
                    id = epParser.parseEditorProtocol(new JSONObject(request.body()), request.cookie("key"), allowOverwrite);
                    return responseJSON("Protokoll \"" + id + "\" erfolgreich in die Datenbank eingefügt", "null");
                case "aItem":
                    id = epParser.parseEditorAgendaItem(new JSONObject(request.body()), request.cookie("key"), allowOverwrite);
                    return responseJSON("Tagesordnungspunkt \"" + id + "\"  erfolgreich in die Datenbank eingefügt", "null");
                case "speech":
                    id = epParser.parseEditorSpeech(new JSONObject(request.body()), request.cookie("key"), allowOverwrite);
                    return responseJSON("Rede \"" + id + "\"  erfolgreich in die Datenbank eingefügt", "null");
                case "person":
                    id = epParser.parseEditorPerson(new JSONObject(request.body()), request.cookie("key"), allowOverwrite);
                    return responseJSON("Person \"" + id + "\"  erfolgreich in die Datenbank eingefügt", "null");
                default:
                    throw new EditorException("editMode must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is " + editMode);
            }
        } catch (Exception e) {
            return responseJSON("Error", e.getMessage());
        }
    };

    /**
     * Gets information for a protocol/agenda item/speech/person and sends it to the page.
     *
     * @author Eric Lakhter
     */
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
                    return responseJSON("Protokoll \"" + id + "\" erfolgreich geladen", epParser.getEditorProtocolFromDB(id));
                case "aItem":
                    return responseJSON("Tagesordnungspunkt \"" + id + "\" erfolgreich geladen", epParser.getEditorAgendaFromDB(id));
                case "speech":
                    return responseJSON("Rede \"" + id + "\" erfolgreich geladen", epParser.getEditorSpeechFromDB(id));
                case "person":
                    return responseJSON("Person \"" + id + "\" erfolgreich geladen", epParser.getEditorPersonFromDB(id));
                default:
                    throw new EditorException("Collection must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is " + col);
            }
        } catch (Exception e) {
            return responseJSON("Error", e.getMessage());
        }
    };

    /**
     * Deletes a protocol/agenda item/speech/person from the database if the user has permission to do so.
     *
     * @author Eric Lakhter
     */
    private static final Route postProtokollEditorDelete = (Request request, Response response) -> {
        try {
            String col = request.queryParams("col");
            if (col == null)
                throw new EditorException("col must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is null");
            String id = request.queryParams("id");
            if (id == null)
                throw new EditorException("id is null");
            String cookie = request.cookie("key");
            switch (col) {
                case "protocol":
                    if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteProtocols")) {
                        epParser.deleteViaEditor(col, id);
                        return responseJSON("Protokoll \"" + id + "\" erfolgreich gelöscht", "null");
                    } else throw new EditorException("Dieser Nutzer hat nicht das Recht Protokolle zu löschen");
                case "aItem":
                    if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteAgendaItems")) {
                        epParser.deleteViaEditor("agendaItem", id);
                        return responseJSON("Tagesordnungspunkt \"" + id + "\" erfolgreich gelöscht", "null");
                    } else
                        throw new EditorException("Dieser Nutzer hat nicht das Recht Tagesordnungspunkte zu löschen");
                case "speech":
                    if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteSpeeches")) {
                        epParser.deleteViaEditor(col, id);
                        return responseJSON("Rede \"" + id + "\" erfolgreich gelöscht", "null");
                    } else throw new EditorException("Dieser Nutzer hat nicht das Recht Reden zu löschen");
                case "person":
                    if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deletePersons")) {
                        epParser.deleteViaEditor(col, id);
                        return responseJSON("Person \"" + id + "\" erfolgreich gelöscht", "null");
                    } else throw new EditorException("Dieser Nutzer hat nicht das Recht Personen zu löschen");
                default:
                    throw new EditorException("Collection must be either \"protocol\", \"aItem\", \"speech\" or \"person\" but is " + col);
            }
        } catch (Exception e) {
            return responseJSON("Error", e.getMessage());
        }
    };

    /**
     * Main dashboard page.
     *
     * @author Andrej Artuschenko
     */
    private static final TemplateViewRoute getDashboard = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        return new ModelAndView(pageContent, "dashboard.ftl");
    };

    /**
     * Route which delivers the data according to the provided query parameters
     *
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

        JSONObject sentimentData = mongoDBHandler.getSentimentData(von, bis, fraction, person, party);
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

    /**
     * Speech network visualisation page.
     *
     * @author Edvin Nise
     */
    private static final TemplateViewRoute getSpeechNetwork = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();
        String von = request.queryParams("von") != null ? request.queryParams("von") : "";
        String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";

        JSONObject networkData = mongoDBHandler.matchSpeakerToDDC(von, bis);

        pageContent.put("redeNetworkData", networkData);

        return new ModelAndView(pageContent, "speechNetwork.ftl");
    };

    /**
     * Comment network visualisation page.
     *
     * @author Edvin Nise
     */
    private static final TemplateViewRoute getCommentNetwork = (Request request, Response response) -> {
        String von = request.queryParams("von") != null ? request.queryParams("von") : "";
        String bis = request.queryParams("bis") != null ? request.queryParams("bis") : "";
        Map<String, Object> pageContent = new HashMap<>();

        JSONObject networkData = mongoDBHandler.commentatorToSpeaker(von, bis);

        pageContent.put("commentNetworkData", networkData);

        return new ModelAndView(pageContent, "commentNetwork.ftl");
    };

    /**
     * Speech/Topic network visualisation page.
     *
     * @author Edvin Nise
     */
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
        if (cookie == null) {
            cookie = "";
        }
        if (mongoDBHandler.checkIfCookieExists(cookie)) {
            pageContent.put("loginStatus", true);
        } else {
            pageContent.put("loginStatus", false);
        }
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editUsers")) {
            pageContent.put("editUserRight", true);
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
        String cookie = request.cookie("key");
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
        String cookie = request.cookie("key");
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
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "addUsers")) {
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
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "editUsers")) {
            if (mongoDBHandler.editUser(oldID, newID, newPassword, newRank)) {
                answer.put("EditSuccess", true);
                return answer;
            }
        }
        answer.put("EditSuccess", false);
        return answer;
    };

    /**
     * provides the feature.ftl as website if the user is allowed to see it.
     *
     * @author Julian Ocker
     */
    private static final TemplateViewRoute getFeatureManagement = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>(0);
        String cookie = request.cookie("key");
        if (cookie == null) {
            cookie = "";
        }
        ArrayList<String> featureList = new ArrayList<>(0);
        mongoDBHandler.getDB().getCollection("features").find().forEach(
                (Consumer<? super Document>) procBlock -> featureList.add(procBlock.getString("_id")));
        pageContent.put("featureList", featureList);
        pageContent.put("editFeatureRight", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editFeatures"));
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editFeatures")) {
            return new ModelAndView(pageContent, "feature.ftl");
        } else {
            return new ModelAndView(pageContent, "noRights.ftl");
        }
    };

    /**
     * accepts oldID newID newPassword new Rank, returns if Success
     *
     * @author Julian Ocker
     */
    private static final Route postEditFeatures = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        String featureToEdit = req.getString("featureToEdit");
        String editRank = req.getString("editRank");
        JSONObject answer = new JSONObject();
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "editFeatures")) {
            if (mongoDBHandler.editFeature(featureToEdit, editRank)) {
                answer.put("EditSuccess", true);
                return answer;
            }
        }
        answer.put("EditSuccess", false);
        return answer;
    };

    /**
     * provides the protocolCheckerLoader as website if the User is an Admin.
     *
     * @author Julian Ocker
     */
    private static final TemplateViewRoute getProtocolCheckerLoader = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>(0);
        String cookie = request.cookie("key");
        if (cookie == null) {
            cookie = "";
        }
        pageContent.put("editFeatureRight", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editFeatures"));
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "admin")) {
            ArrayList<File> options = new ArrayList<>(Arrays.asList(XMLProtocolParser.getAllFiles()));
            File[] files = XMLProtocolParser.getArrayOfNewProtocols();
            List<String> parsedProtocols = XMLProtocolParser.getListOfParsedProtocols();

            Integer filesNumber = files.length;
            Integer parsedProtocolNumber = parsedProtocols.size();
            pageContent.put("options", options);
            pageContent.put("numberOfProtocols", filesNumber);
            pageContent.put("numberOfParsedProtocols", parsedProtocolNumber);
            return new ModelAndView(pageContent, "protocolCheckerLoader.ftl");
        } else {
            return new ModelAndView(pageContent, "noRights.ftl");
        }
    };

    /**
     * accepts trigger, starts loading all Files
     *
     * @author Julian Ocker
     */
    private static final Route postLoadAll = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        JSONObject answer = new JSONObject();
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "admin")) {
            Scraper.downloadAllXMLs();
        }
        answer.put("EditSuccess", true);
        return answer;
    };

    /**
     * accepts trigger, starts the parsing of all Files
     *
     * @author Julian Ocker
     */
    private static final Route postParseAll = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        JSONObject answer = new JSONObject();
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "admin")) {
            if (XMLProtocolParser.parserStarterGenerell()) {
                answer.put("EditSuccess", true);
                return answer;
            }
        }
        answer.put("EditSuccess", false);
        return answer;
    };

    /**
     * accepts trigger, starts the Parsing of new Files
     *
     * @author Julian Ocker
     */
    private static final Route postParseNew = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        JSONObject answer = new JSONObject();
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "admin")) {
            if (XMLProtocolParser.parserStarterNewProtocols()) {
                answer.put("EditSuccess", true);
                return answer;
            }
        }
        answer.put("EditSuccess", false);
        return answer;
    };

    /**
     * accepts trigger and filename, starts the Parsing the file
     *
     * @author Julian Ocker
     */
    private static final Route postParseSingle = (Request request, Response response) -> {
        JSONObject req = new JSONObject(request.body());
        String fileToParse = req.getString("protocolToParse");
        JSONObject answer = new JSONObject();
        if (fileToParse == null || fileToParse.isEmpty() || fileToParse.equals("undefined")) {
            return answer.put("EditSuccess", false);
        }
        if (mongoDBHandler.checkIfCookieIsAllowedAFeature(request.cookie("key"), "admin")) {
            if (XMLProtocolParser.parserStarterSingle(fileToParse)) {
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
     * Puts all editor-related permissions into the given map based on given user rank.
     *
     * @param cookie Rank of the user requesting permissions.
     * @return JSON with permissions
     * @author Eric Lakhter
     */
    private static JSONObject getEditorPermissions(String cookie) {
        JSONObject permissions = new JSONObject();
        permissions.put("addProtocols", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "addProtocols"));
        permissions.put("deleteProtocols", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteProtocols"));
        permissions.put("editProtocols", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editProtocols"));

        permissions.put("addAgendaItems", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "addAgendaItems"));
        permissions.put("deleteAgendaItems", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteAgendaItems"));
        permissions.put("editAgendaItems", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editAgendaItems"));

        permissions.put("addSpeeches", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "addSpeeches"));
        permissions.put("deleteSpeeches", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deleteSpeeches"));
        permissions.put("editSpeeches", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editSpeeches"));

        permissions.put("addPersons", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "addPersons"));
        permissions.put("deletePersons", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "deletePersons"));
        permissions.put("editPersons", mongoDBHandler.checkIfCookieIsAllowedAFeature(cookie, "editPersons"));
        return permissions;
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
        Runtime rt = Runtime.getRuntime();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else if (os.contains("mac")) {
            rt.exec("open " + url);
        } else if (os.contains("nix") || os.contains("nux")) {
            rt.exec("xdg-open " + url);
        }
    }
}
