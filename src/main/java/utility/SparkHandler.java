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
import java.util.Map;

import static spark.Spark.*;

/**
 * Starts the localhost server for the protocol visualisation.
 * @author Eric Lakhter
 */
@Testing
@Unfinished("Just a skeleton so far")
public class SparkHandler {
    private static MongoDBHandler mongoDBHandler = null;
    private static final Configuration cfg = Configuration.getDefaultConfiguration();
    private static String path = "C:/Users/ericl/Desktop/Uni/5_Semester/PPR/parliament_browser_9_donnerstag_4/src/main/resources/";

    public static void main(String[] args) throws IOException {
        SparkHandler.init();
        openInDefaultBrowser("http://localhost:4567/");
    }

    /**
     * Sets up the website's paths.
     * @see #getHome
     * @author Eric Lakhter
     */
    public static void init() throws IOException {
        try {
            mongoDBHandler = new MongoDBHandler();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cfg.setDirectoryForTemplateLoading(new File(path));
        cfg.setDefaultEncoding("UTF-8");
        port(4567);
        before((req, res) -> {
            String path = req.pathInfo();
            if (!path.endsWith("/") && req.queryParams().size() == 0) res.redirect(path + "/") ;
        });

        get("/test/", getTest, new FreeMarkerEngine(cfg));
        get("/", getHome, new FreeMarkerEngine(cfg));
    }

    /*
        GET routes
     */


    /**
     * Test page.
     *
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

        return new ModelAndView(pageContent, "frontend/test.ftl");
    };

    /**
     * Homepage.
     *
     * @author Eric Lakhter
     */
    private static final TemplateViewRoute getHome = (Request request, Response response) -> {
        Map<String, Object> pageContent = new HashMap<>();

        pageContent.put("title", "Homepage");

        return new ModelAndView(pageContent, "frontend/home.ftl");
    };

    /**
     * Opens <a href="http://localhost:4567/">http://localhost:4567/</a> in the system's default browser.
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

//    @Override
//    public void destroy() {
//        SparkApplication.super.destroy();
//    }

}
