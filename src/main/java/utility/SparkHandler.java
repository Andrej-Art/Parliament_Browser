package utility;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;
import spark.servlet.SparkApplication;
import spark.template.freemarker.FreeMarkerEngine;
import utility.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

/**
 * Starts the localhost server for the protocol visualisation.
 * @author Eric Lakhter
 */
@Testing
@Unfinished("Just a skeleton so far")
public class SparkHandler implements SparkApplication {
    private static MongoDBHandler mongoDBHandler = null;
    private static final Configuration cfg = Configuration.getDefaultConfiguration();

    /**
     * Sets up the website's paths.
     * @see #getHome
     * @author Eric Lakhter
     */
    public void init() {
        try {
            mongoDBHandler = new MongoDBHandler();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        get("/", getHome, new FreeMarkerEngine(cfg));
    }

    /*
        GET routes
     */

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

    @Override
    public void destroy() {
        SparkApplication.super.destroy();
    }

}
