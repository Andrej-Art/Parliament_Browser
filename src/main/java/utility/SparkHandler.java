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
        get("/reden/", getSpeechVis, new FreeMarkerEngine(cfg));
    }

    /*
     * GET routes
     */


    /**
     * Test page.
     * @author Eric Lakhter
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

        return new ModelAndView(pageContent, "test.ftl");
    };
    @Testing
    private static final TemplateViewRoute getMulti = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

       // JSONObject entitiesObject = mongoDBHandler.facetNamedEntities("", "");
       // pageContent.put("entities", entitiesObject);

        List<JSONObject> posAndCounts = mongoDBHandler.getPOSCount();
        pageContent.put("pos", posAndCounts);

        List<JSONObject> tokenAndCounts = mongoDBHandler.getTokenCount(30);
        pageContent.put("token", tokenAndCounts);

        JSONObject datesAndNamedEntities = mongoDBHandler.getNamedEntityCount("","");
        pageContent.put("entities", datesAndNamedEntities);
        return new ModelAndView(pageContent, "multi.ftl");
    };

    @Testing
    private static final TemplateViewRoute getDashboard = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

      //  JSONObject entitiesObject = mongoDBHandler.facetNamedEntities("", "");
     //   pageContent.put("entities", entitiesObject);
        return new ModelAndView(pageContent, "dashboard.ftl");
    };

    /**
     * Homepage.
     * @author Eric Lakhter
     */
    private static final TemplateViewRoute getHome = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        pageContent.put("title", "Homepage");

        return new ModelAndView(pageContent, "home.ftl");
    };

    /**
     * Speech visualisation page.
     * @author Eric Lakhter
     */
    private static final TemplateViewRoute getSpeechVis = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

//       String speechID = request.queryParams("speechID") != null ? request.queryParams("speechID") : "";
        String speechID = "ID19100100";

        List<JSONObject> speechData = mongoDBHandler.allSpeechData(speechID);
        pageContent.put("speechData", speechData);

        return new ModelAndView(pageContent, "speech_vis.ftl");
    };

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
//        System.out.println(os);
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
