package utility;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * In this class, the website of the German Bundestag is accessed.
 * More specifically, the Open Data page of the Bundestag, where we fetch the protocols
 * and other necessary information for our analysis.
 *
 * @author Andrej Artuschenko
 */

public class Scraper {

    /**
     * Method to download all necessary files
     * Orientation: https://jsoup.org
     * Access is divided into three cases: dtd file (may not be needed), Stammdaten data and XML-protocols (19. and 20. legislature)
     */
    public static void downloadAllXMLs() {

        try {
            /*
             * Here a connection to the Bundestag website is established, where the required documents are located.
             */
            Document opendataHTML = Jsoup.connect("https://www.bundestag.de/services/opendata").get();

            //get MDB-Stammdaten from Bundestagswebsite and the belonging dtd-file
            Elements furtherInfoElementS = opendataHTML.getElementsByClass("bt-link-dokument");
            for (Element furtherInfoElem : furtherInfoElementS) {

                /*
                 * case dtd-file
                 */
                if (Pattern.matches("DTD für Plenarprotokolle des Deutschen Bundestags, gültig ab 19\\. Wahlperiode.*", furtherInfoElem.attr("title"))) {
                    Element dtdElem = furtherInfoElem;
                    try {

                        // source: https://jsoup.org/apidocs/org/jsoup/nodes/Node.html#absUrl%28java.lang.String%29
                        // abs = absolute key
                        URL dtdURL = new URL(dtdElem.attr("abs:href"));
                        File dtdFile = new File("ProtokollXMLs/dbtplenarprotokoll.dtd");
                        System.out.println("Downloading the DTD-file from " + dtdURL + " at " + dtdFile);

                        FileUtils.copyURLToFile(dtdURL, dtdFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                /**
                 * case for mdb-Stammdaten
                 */

                if (Pattern.matches("Stammdaten aller Abgeordneten seit 1949 im XML-Format.*", furtherInfoElem.attr("title"))) {
                    Element mdbElem = furtherInfoElem;
                    try {
                        URL mdbURL = new URL(mdbElem.attr("abs:href"));

                        // place downloaded data in the newly created folder
                        File mdbFile = new File("ProtokollXMLs/MdB-Stammdaten-data.zip");
                        System.out.println("Downloading mdb-Stammdateen from " + mdbURL + " at " + mdbFile);
                        FileUtils.copyURLToFile(mdbURL, mdbFile);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    /*
                     * Unzip mdb-Stammdaten and save in folder
                     * source: https://stackoverflow.com/questions/10633595/java-zip-how-to-unzip-folder
                     */
                    ZipFile zipFile = new ZipFile("ProtokollXMLs/MdB-Stammdaten-data.zip");
                    zipFile.extractAll("ProtokollXMLs/MdB-Stammdaten-data/");
                }
            }

            /*
             * The tag section is used to select the download section of each legislative.
             * we iterate through the individual elements in the individual sections of the 19th and 20th periods
             */

            // Downloading XML-protocols
            Elements sectionElementS = opendataHTML.getElementsByTag("section");
            for (Element sectionElem : sectionElementS) {
                // bt-title are searched by election period
                String bttitle = sectionElem.getElementsByClass("bt-title").text();
                if (bttitle.matches("Plenarprotokolle der 19. Wahlperiode|Plenarprotokolle der 20. Wahlperiode")) {

                    /*
                     * mod is the id of our sections. for example the id = mod543410 designates the last 10 protocolls of the 19 legislative
                     *  https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410
                     *  Each section contains 10 protocols, which we collect one by one. The maxoffset variable stores this.
                     */

                    String modid = (sectionElem.attr("id")).replace("mod", "");
                    int maxoffset = 0;
                    while (true) {
                        String url = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/" + modid + "-" + modid + "?limit=10&noFilterSet=true&offset=" + maxoffset;

                        /*
                         * counting maxoffset until url doesn't return results - then download backwards from the maxoffset
                         */

                        if (checkSite(url)) {
                            maxoffset += 10;
                        } else {
                            maxoffset = maxoffset - 10;
                            for (int offset = maxoffset; offset >= 0; offset = offset - 10) {
                                url = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/" + modid + "-" + modid + "?limit=10&noFilterSet=true&offset=" + offset;
                                downloadTenXML(url);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Download exact 10 xml-protocols from the section
     */
    private static int downloadTenXML(String url) {
        int downloadCounter = 0;
        try {
            // inspect the website and access body elements using tbody where the xml protocols are stored
            Document siteHTML = Jsoup.connect(url).get();
            Elements tbodyElementS = siteHTML.getElementsByTag("tbody");
            for (Element tbodyElement : tbodyElementS) {
                Elements xmlrows = tbodyElement.getElementsByTag("a");
                for (int a = xmlrows.size() - 1; a >= 0; a--) {
                    try {
                        URL xmlURL = new URL(xmlrows.get(a).attr("abs:href"));
                        File xmlFile = new File("ProtokollXMLs/" + getFileName(xmlrows.get(a).attr("abs:href")) + ".xml");
                        System.out.println("Downloading XML from " + xmlURL + " at " + xmlFile);
                        FileUtils.copyURLToFile(xmlURL, xmlFile);
                        downloadCounter += 1;

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return downloadCounter;
    }

    /**
     * Method to get the Filename from URL
     */
    private static String getFileName(String url) {
        Pattern regex = Pattern.compile(".*/(.*?)\\.xml");
        Matcher regexMatcher = regex.matcher(url);
        regexMatcher.find();
        String fileName = regexMatcher.group(1);
        return fileName;
    }

    /**
     * Method to check the Website (URL) if there is an error (no more files)
     *
     * @param url
     * @return boolean
     */
    private static boolean checkSite(String url) {
        try {
            Document siteHTML = Jsoup.connect(url).get();
            Elements errors = siteHTML.getElementsByClass("col-xs-12 bt-slide-error bt-slide");
            if (errors.size() >= 1) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
